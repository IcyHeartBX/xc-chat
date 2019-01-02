package com.pix.xcserverlibrary.tcp;


import com.pix.xcserverlibrary.utils.LogUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.*;

/**
 *
 * @author pix
 * @version 1.0.0
 * @description
 * @modify
 */
public class TcpNIOSocketClient implements ITcpSocketClient{


    private static final String TAG = "TcpNIOSocketClient";
    private String mHost;
    private int mPort;
    private SocketChannel mSocketChannel;

    private ExecutorService sExecutorServices = Executors.newCachedThreadPool();//线程池用来服用资源


    private TcpSocketListener mTcpNioListener;
    private volatile Future mSendFuture;
    private volatile Future mReceiveFuture;
    private SendThread mSendThread;
    private ReceiveThread mReceiveThread;
    private volatile boolean mIsConnected;
    private boolean mDestroy;
    private static volatile ITcpSocketClient singleInstance = null;

    public static ITcpSocketClient getSingleInstance() {
        if (null == singleInstance) {
            //  synchronized (TcpNIOSocketClient.class) {
            //  if (null == singleInstance) {
            singleInstance = new TcpNIOSocketClient();
            //}
            //}
        }
        return singleInstance;
    }


    public TcpNIOSocketClient() {
        mSendThread = new SendThread();
        mReceiveThread = new ReceiveThread();
        mDestroy = false;
        mIsConnected = false;
    }

    public void setHostAndPort(String host, int port) {
        mHost = host;
        mPort = port;
    }

    public void setBlocking(boolean blocking) throws IOException {
        if (mDestroy) return;
        mSocketChannel.configureBlocking(blocking);
    }

    public synchronized void sendData(byte[] bs) {
        if (mDestroy) return;
        try {
            mSendThread.waitSendQueue.put(bs);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return mIsConnected;
    }


    public synchronized void connect() {
        LogUtils.d(TAG, "connect(),star...");
        try {
            if (mIsConnected) {
                return;
            }
            // 等待线程结束,防止异步错误
            while (!isThreadStop()) {}
            mSocketChannel = SocketChannel.open();
            mSocketChannel.configureBlocking(true);
            LogUtils.d(TAG, "connect(),开始连接：" + this.mHost + ":" + this.mPort);
            mSocketChannel.connect(new InetSocketAddress(mHost, mPort));
            mReceiveThread = new ReceiveThread();
            mSendThread = new SendThread();
            mReceiveFuture =  sExecutorServices.submit(mReceiveThread);
            mSendFuture = sExecutorServices.submit(mSendThread);
            LogUtils.d(TAG, "connect(), end：" + this.mHost + ":" + this.mPort);
        } catch (IOException e) {
            LogUtils.d("TcpNIOSocketClient", "连接错误。。。" + e.getMessage());
            close();
            if (mTcpNioListener != null) {
                mTcpNioListener.onConnectError(this, e.getMessage(), e);
            }
        } catch (Exception ex) {
            LogUtils.d("TcpNIOSocketClient", "连接错误。。。" + ex.getMessage());
            if (mTcpNioListener != null) {
                mTcpNioListener.onConnectError(this, ex.getMessage(), ex);
            }
            close();
            error("发生错误，关闭", ex);
        }
        LogUtils.d(TAG, "connect(),end...");
    }


    public void close(boolean flush) {
        if (flush) {
            mSendThread.flushEnd = true;
        } else {
            close();
        }
    }

    public  synchronized void close() {
        try {
            if (mIsConnected) {
                mIsConnected = false;
                if (mSocketChannel != null) {
                    mSocketChannel.close();
                    mSocketChannel = null;
                }
            }
            mReceiveThread.running = false;
            mSendThread.running = false;
            mReceiveThread.mByteBuffer.clear();
            mSendThread.mByteBuffer.clear();
            mSendThread.waitSendQueue.clear();

        } catch (IOException e) {
            LogUtils.d(TAG, "close异常");
        }
        LogUtils.d(TAG, "close(),end...");
    }

    /**
     * 发送接收线程是否停止
     * @return
     */
    public boolean isThreadStop() {
        try {
            if((null == mSendFuture ||null == mSendFuture.get())
                    &&(null == mReceiveFuture || null == mReceiveFuture.get())) {
                return true;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

    public synchronized void destroy() {
        LogUtils.d(TAG, "destroy()");
//        mDestroy = true;
    }

    private void debug(String message) {
        LogUtils.d(TAG, message);
    }

    private void error(String message, Throwable throwable) {
        LogUtils.d(TAG, message);
    }

    public void setTcpNioListener(TcpSocketListener mTcpNioListener) {
        this.mTcpNioListener = mTcpNioListener;
    }

    private class SendThread implements Runnable {

        private volatile boolean running = true;
        private ByteBuffer mByteBuffer = ByteBuffer.allocate(1024);
        private volatile boolean flushEnd = false;
        private final BlockingQueue<byte[]> waitSendQueue = new LinkedBlockingQueue<>();

        @Override
        public void run() {
            if (LogUtils.ISDEBUG) {
                LogUtils.d(TAG, "发送线程开始执行");
            }
            while (running) {
                try {
//                    if(LogUtils.ISDEBUG){
//                        LogUtils.d(TAG,"发送线程running");
//                    }
                    if (!mIsConnected && !mDestroy) {
                        while (null != mSocketChannel && !mSocketChannel.finishConnect()) {
                            LogUtils.d(TAG, "连接中...");
                            if (!running) {
                                break;
                            }
                            if (flushEnd) {
                                close();
                                break;
                            }
                        }
                        LogUtils.d(TAG, "连接成功");
                        //连接成功回调
                        mIsConnected = true;
                        if (mTcpNioListener != null) {
                            mTcpNioListener.onConnect(TcpNIOSocketClient.this);
                        }
                    }
                    if (!running) {
                        break;
                    }
                    byte[] data = waitSendQueue.poll(1000, TimeUnit.MILLISECONDS);
                    if (data != null && mIsConnected) {
                        mByteBuffer.put(data);
                        mByteBuffer.flip();
                    } else {
                        if (flushEnd) {
//                            do {
                            while (true) {
                                data = waitSendQueue.poll();
                                if (null != data) {
                                    mByteBuffer.put(data);
                                    mByteBuffer.flip();
                                    while (mByteBuffer.hasRemaining() && mIsConnected) {
                                        mSocketChannel.write(mByteBuffer);
                                    }
                                    mByteBuffer.clear();
                                } else {
                                    break;
                                }
                            }
                            close();
                            break;
                        }
                        continue;
                    }
                    while (mByteBuffer.hasRemaining() && mIsConnected) {
                        mSocketChannel.write(mByteBuffer);
                    }
                    mByteBuffer.clear();
                } catch (ClosedChannelException cce) {
                    LogUtils.d("TcpNIOSocketClient", "send ClosedChannelException:" + cce.toString());
                } catch (IOException e) {
                    LogUtils.d("TcpNIOSocketClient", "send IOException:" + e.toString());
                    close();
                    if (mTcpNioListener != null) {
                        mTcpNioListener.onConnectError(TcpNIOSocketClient.this, e.getMessage(), e);
                    }
                } catch (InterruptedException e) {
                    LogUtils.d("TcpNIOSocketClient", "send InterruptedException:" + e.toString());
                    LogUtils.w(TAG, "发送线程打断错误.." + e.getMessage());
                } catch (Exception ex) {
                    LogUtils.d("TcpNIOSocketClient", "send Exception:" + ex.toString());
                    close();
                    error("发生错误，关闭", ex);
                }
            }
            if (LogUtils.ISDEBUG) {
                LogUtils.d(TAG, "发送线程end");
            }
        }
    }


    private class ReceiveThread implements Runnable {
        private volatile boolean running = true;
        private ByteBuffer mByteBuffer = ByteBuffer.allocate(1024);//两个大小最好一样

        @Override
        public void run() {
            byte[] data;
            if (LogUtils.ISDEBUG) {
                LogUtils.d(TAG, "接收线程开始执行");
            }
            while (running) {
                try {
//                    if(LogUtils.ISDEBUG){
//                        LogUtils.d(TAG,"接收线程running");
//                    }
                    if (mIsConnected && mSocketChannel != null && mSocketChannel.isConnected()) {
                        int length = mSocketChannel.read(mByteBuffer);
                        if (length == -1 || length == 0) continue;
                        data = new byte[length];
                        mByteBuffer.flip();
                        mByteBuffer.get(data, 0, length);
                        if (mTcpNioListener != null) {
                            mTcpNioListener.onReceiveData(TcpNIOSocketClient.this, data);
                        }

                        mByteBuffer.compact();
                    }
                } catch (ClosedChannelException cce) {
                    LogUtils.d("TcpNIOSocketClient", "receive ClosedChannelException:" + cce.toString());
                } catch (IOException e) {
                    LogUtils.d("TcpNIOSocketClient", "receive IOException:" + e.toString());
                    close();
                    running = false;
                    if (mTcpNioListener != null) {
                        mTcpNioListener.onConnectError(TcpNIOSocketClient.this, e.getMessage(), e);
                    }
                } catch (Exception ex) {
                    LogUtils.d("TcpNIOSocketClient", "receive Exception:" + ex.toString());
                    close();
                    error("发生错误，关闭", ex);
                }
            }
            if (LogUtils.ISDEBUG) {
                LogUtils.d(TAG, "接收线程end");
            }
        }
    }
}
