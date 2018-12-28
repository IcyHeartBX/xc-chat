package com.pix.xcserverlibrary.pack;

public interface PackConstants {
	public static final String PACK_ENCODE = "UTF-16LE";

	//包头长度
	public static final int DEF_PACK_HEAD_LEN = 6;
	//包尾长度
	public static final int DEF_PACK_TAIL_LEN = 4;
	/** 包体最大长度 */
	public final static short DEF_MAX_TCPPACK_LEN = 2048;

	public final static byte[] constPackHead = { 3, 2 };
	public final static byte[] constPackTail = { 5, 4 };

}
