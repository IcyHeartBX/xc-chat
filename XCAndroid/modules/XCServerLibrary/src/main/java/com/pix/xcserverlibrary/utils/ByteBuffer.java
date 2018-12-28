package com.pix.xcserverlibrary.utils;

import java.io.EOFException;
import java.io.IOException;
import java.io.UTFDataFormatException;
import java.math.BigDecimal;

/**
 *
 * @modify
 * @version 1.0.0
 */
public class ByteBuffer {
	private byte[] buf;
	private int pos;//写入时含义也为buf的长度
	private int count;//读取时的buf长度

	public ByteBuffer() {
		this(64);
	}

	public ByteBuffer(int initialCapacity) {
		buf = new byte[initialCapacity];
		pos = 0;
	}

	public ByteBuffer(byte[] buf) {
		this.buf = buf;
		pos = 0;
		count = buf.length;
	}

	public ByteBuffer(byte[] buf, int offset, int length) {
		this.buf = buf;
		this.pos = offset;
		this.count = Math.min(offset + length, buf.length);
	}

	public static byte[] copyOf(byte[] original, int newLength) {
		byte[] copy = new byte[newLength];
		System.arraycopy(original, 0, copy, 0, Math.min(original.length, newLength));
		return copy;
	}

	public synchronized void writeByte(byte b) {
		int newCount = pos + 1;
		if (newCount > buf.length) {
			buf = copyOf(buf, Math.max(buf.length << 1, newCount));
		}
		buf[pos] = b;
		pos = newCount;
	}

	public synchronized void write(byte b[], int off, int len) {
		if ((off < 0) || (off > b.length) || (len < 0) || ((off + len) > b.length) || ((off + len) < 0)) {
			throw new IndexOutOfBoundsException();
		}
		else if (len == 0) {
			return;
		}
		int newCount = pos + len;
		if (newCount > buf.length) {
			buf = copyOf(buf, Math.max(buf.length << 1, newCount));
		}
		System.arraycopy(b, off, buf, pos, len);
		pos = newCount;
	}

	private byte writeBuffer[] = new byte[8];

	public void writeShort(Short v) {
		writeBuffer[0] = (byte) (0xff & v);
		writeBuffer[1] = (byte) ((0xff00 & v) >>> 8);
//		writeBuffer[0] = (byte) (v >>> 8);
//		writeBuffer[1] = (byte) (v >>> 0);
		write(writeBuffer, 0, 2);
	}
	public void writeShortBE(Short v) {
		writeBuffer[0] = (byte) ((0xff00 & v)>>> 8);
		writeBuffer[1] = (byte) ((0xff & v) );
//		writeBuffer[0] = (byte) (v >>> 8);
//		writeBuffer[1] = (byte) (v >>> 0);
		write(writeBuffer, 0, 2);
	}
	public void writeBoolean(boolean v) {
		writeByte((byte) (v ? 1 : 0));
	}

	public final void writeInt(int v) {
//		writeBuffer[0] = (byte) (v >>> 24);
//		writeBuffer[1] = (byte) (v >>> 16);
//		writeBuffer[2] = (byte) (v >>> 8);
//		writeBuffer[3] = (byte) (v >>> 0);
		writeBuffer[0] = (byte) (0xff & v);
		writeBuffer[1] = (byte) ((0xff00 & v) >>> 8);
		writeBuffer[2] = (byte) ((0xff0000 & v) >>> 16);
		writeBuffer[3] = (byte) ((0xff000000 & v) >>> 24);
		write(writeBuffer, 0, 4);
	}

	public final void writeMidum(int v){
		writeBuffer[0] = (byte) (0xff & v);
		writeBuffer[1] = (byte) ((0xff00 & v) >>> 8);
		writeBuffer[2] = (byte) ((0xff0000 & v) >>> 16);
		write(writeBuffer, 0, 3);
	}

	public final void writeLong(long v) {
//		writeBuffer[0] = (byte) (v >>> 56);
//		writeBuffer[1] = (byte) (v >>> 48);
//		writeBuffer[2] = (byte) (v >>> 40);
//		writeBuffer[3] = (byte) (v >>> 32);
//		writeBuffer[4] = (byte) (v >>> 24);
//		writeBuffer[5] = (byte) (v >>> 16);
//		writeBuffer[6] = (byte) (v >>> 8);
//		writeBuffer[7] = (byte) (v >>> 0);
		writeBuffer[0] = (byte) (0xff & v);
		writeBuffer[1] = (byte) ((0xff00 & v) >>> 8);
		writeBuffer[2] = (byte) ((0xff0000 & v) >>> 16);
		writeBuffer[3] = (byte) ((0xff000000 & v) >>> 24);
		writeBuffer[4] = (byte) ((0xff00000000L & v) >>> 32);
		writeBuffer[5] = (byte) ((0xff0000000000L & v) >>> 40);
		writeBuffer[6] = (byte) ((0xff000000000000L & v) >>> 48);
		writeBuffer[7] = (byte) ((0xff00000000000000L & v) >>> 56);
		write(writeBuffer, 0, 8);
	}

	public final void writeFloat(float v) {
		writeInt(Float.floatToIntBits(v));
	}

	public final void writeDouble(double v) {
		writeLong(Double.doubleToLongBits(v));
	}

	public int writeUTF(String str) throws IOException {
		int strlen = str.length();
		int utflen = 0;
		int c, count = 0;

		/* use charAt instead of copying String to char array */
		for (int i = 0; i < strlen; i++) {
			c = str.charAt(i);
			if ((c >= 0x0001) && (c <= 0x007F)) {
				utflen++;
			}
			else if (c > 0x07FF) {
				utflen += 3;
			}
			else {
				utflen += 2;
			}
		}

		if (utflen > 65535)
			throw new UTFDataFormatException("encoded string too long: " + utflen + " bytes");

		byte[] bytearr = new byte[utflen + 2];

		//bytearr[count++] = (byte) ((utflen >>> 8) & 0xFF);
		//bytearr[count++] = (byte) ((utflen >>> 0) & 0xFF);
		bytearr[count++] = (byte) (0xff & utflen);
		bytearr[count++] = (byte) ((0xff00 & utflen) >>> 8);

		int i = 0;
		for (i = 0; i < strlen; i++) {
			c = str.charAt(i);
			if (!((c >= 0x0001) && (c <= 0x007F)))
				break;
			bytearr[count++] = (byte) c;
		}

		for (; i < strlen; i++) {
			c = str.charAt(i);
			if ((c >= 0x0001) && (c <= 0x007F)) {
				bytearr[count++] = (byte) c;

			}
			else if (c > 0x07FF) {
				bytearr[count++] = (byte) (0xE0 | ((c >> 12) & 0x0F));
				bytearr[count++] = (byte) (0x80 | ((c >> 6) & 0x3F));
				bytearr[count++] = (byte) (0x80 | ((c >> 0) & 0x3F));
			}
			else {
				bytearr[count++] = (byte) (0xC0 | ((c >> 6) & 0x1F));
				bytearr[count++] = (byte) (0x80 | ((c >> 0) & 0x3F));
			}
		}
		write(bytearr, 0, utflen + 2);
		return utflen + 2;
	}

	public synchronized byte toByteArray()[] {
		return copyOf(buf, pos);
	}

	///////////////////////////
	///读取的方法
	//////////////////////////////
	public synchronized int read() {
		return (pos < count) ? (buf[pos++] & 0xff) : -1;
	}


	public synchronized int read(byte b[], int off, int len) {
		if (b == null) {
			throw new NullPointerException();
		}
		else if (off < 0 || len < 0 || len > b.length - off) {
			throw new IndexOutOfBoundsException();
		}
		if (pos >= count) {
			return -1;
		}
		if (pos + len > count) {
			len = count - pos;
		}
		if (len <= 0) {
			return 0;
		}
		System.arraycopy(buf, pos, b, off, len);
		pos += len;
		return len;
	}

	public synchronized long skip(long n) {
		if (pos + n > count) {
			n = count - pos;
		}
		if (n < 0) {
			return 0;
		}
		pos += n;
		return n;
	}

	public synchronized int available() {
		return count - pos;
	}

	public final boolean readBoolean() throws IOException {
		int ch = read();
		if (ch < 0)
			throw new EOFException();
		return (ch != 0);
	}

	public final int readMidum()throws IOException {
//		byte[] bytes=new byte[3];
		read(readBuffer,0,3);
		return (readBuffer[0]) | (readBuffer[1]&0xff)<<8 | ((readBuffer[2]&0xff)<<16);
	}

	public final byte readByte() throws IOException {
		int ch = read();
		if (ch < 0)
			throw new EOFException();
		return (byte) (ch);
	}

	public final int readUnsignedByte() throws IOException {
		int ch = read();
		if (ch < 0)
			throw new EOFException();
		return ch;
	}

	private byte readBuffer[] = new byte[8];

	public final short readShort() throws IOException {
		if (read(readBuffer, 0, 2) < 0) {
			throw new EOFException();
		}
		return (short) (((short) readBuffer[0] & 0xff | ((short) readBuffer[1] & 0xff) << 8));
	}
	public final short readShortBE() throws IOException {
		if (read(readBuffer, 0, 2) < 0) {
			throw new EOFException();
		}
		return (short) (((short) readBuffer[1] & 0xff | ((short) readBuffer[0] & 0xff) << 8));
	}

	public final int readUnsignedShort() throws IOException {
		if (read(readBuffer, 0, 2) < 0) {
			throw new EOFException();
		}
		return (short) ((readBuffer[0] & 0xff) | ((buf[1] & 0xff) << 8));
	}

	public final char readChar() throws IOException {
		int ch1 = read();
		int ch2 = read();
		if ((ch1 | ch2) < 0) {
			throw new EOFException();
		}
		return (char) ((ch1 << 8) + (ch2 << 0));
	}

	public final int readInt() throws IOException {
		if (read(readBuffer, 0, 4) < 0) {
			throw new EOFException();
		}
		//return (((int) (readBuffer[0] & 255) << 24) + ((readBuffer[1] & 255) << 16) + ((readBuffer[2] & 255) << 8) + ((readBuffer[3] & 255) << 0));
		return ((((int) readBuffer[0] & 0xff)) | (((int) readBuffer[1] & 0xff) << 8) | (((int) readBuffer[2] & 0xff) << 16) | (((int) readBuffer[3] & 0xff) << 24));
	}

	public final long readLong() throws IOException {
		if (read(readBuffer, 0, 8) < 0) {
			throw new EOFException();
		}
		return ((((long) readBuffer[0] & 0xff) << 0) | (((long) readBuffer[1] & 0xff) << 8) | (((long) readBuffer[2] & 0xff) << 16)
				| (((long) readBuffer[3] & 0xff) << 24) | (((long) readBuffer[4] & 0xff) << 32) | (((long) readBuffer[5] & 0xff) << 40)
				| (((long) readBuffer[6] & 0xff) << 48) | (((long) readBuffer[7] & 0xff) << 56));
	}

	public static final BigDecimal readUnsignedLong(long value) throws IOException {
		if (value >= 0)
			return new BigDecimal(value);
		long lowValue = value & 0x7fffffffffffffffL;
		return BigDecimal.valueOf(lowValue).add(BigDecimal.valueOf(Long.MAX_VALUE)).add(BigDecimal.valueOf(1));
	}
	
	public static final int readUnsignedByte(byte value){
		return value & 0xff;
	}

	public final float readFloat() throws IOException {
		return Float.intBitsToFloat(readInt());
	}

	public final double readDouble() throws IOException {
		return Double.longBitsToDouble(readLong());
	}

	public final String readUTF() throws IOException {
		int utflen = readUnsignedShort();

		byte[] bytearr = new byte[utflen];
		char[] chararr = new char[utflen];

		int c, char2, char3;
		int count = 0;
		int chararr_count = 0;

		read(bytearr, 0, utflen);

		while (count < utflen) {
			c = (int) bytearr[count] & 0xff;
			if (c > 127)
				break;
			count++;
			chararr[chararr_count++] = (char) c;
		}

		while (count < utflen) {
			c = (int) bytearr[count] & 0xff;
			switch (c >> 4) {
				case 0:
				case 1:
				case 2:
				case 3:
				case 4:
				case 5:
				case 6:
				case 7:
					/* 0xxxxxxx*/
					count++;
					chararr[chararr_count++] = (char) c;
					break;
				case 12:
				case 13:
					/* 110x xxxx   10xx xxxx*/
					count += 2;
					if (count > utflen)
						throw new UTFDataFormatException("malformed input: partial character at end");
					char2 = (int) bytearr[count - 1];
					if ((char2 & 0xC0) != 0x80)
						throw new UTFDataFormatException("malformed input around byte " + count);
					chararr[chararr_count++] = (char) (((c & 0x1F) << 6) | (char2 & 0x3F));
					break;
				case 14:
					/* 1110 xxxx  10xx xxxx  10xx xxxx */
					count += 3;
					if (count > utflen)
						throw new UTFDataFormatException("malformed input: partial character at end");
					char2 = (int) bytearr[count - 2];
					char3 = (int) bytearr[count - 1];
					if (((char2 & 0xC0) != 0x80) || ((char3 & 0xC0) != 0x80))
						throw new UTFDataFormatException("malformed input around byte " + (count - 1));
					chararr[chararr_count++] = (char) (((c & 0x0F) << 12) | ((char2 & 0x3F) << 6) | ((char3 & 0x3F) << 0));
					break;
				default:
					/* 10xx xxxx,  1111 xxxx */
					throw new UTFDataFormatException("malformed input around byte " + count);
			}
		}
		// The number of chars produced may be less than utflen
		return new String(chararr, 0, chararr_count);
	}

	public static String readGB2312String(ByteBuffer data) throws IOException {
		short len = data.readShort();
		if (len == 0)
			return "";
		byte[] temp = new byte[len];
		data.read(temp, 0, len);
		return new String(temp, "GB2312");
	}
}
