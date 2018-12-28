package com.pix.xcserverlibrary.pack;



import com.pix.xcserverlibrary.utils.ByteBuffer;

import java.io.IOException;
import java.io.Serializable;

public abstract class BaseStruct implements Serializable {
	private static final long serialVersionUID = 1L;

	public abstract void serialize(ByteBuffer buf) throws IOException;
}
