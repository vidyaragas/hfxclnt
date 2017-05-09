package com.ajna.hfxclnt.connection;

import java.nio.ByteBuffer;

public interface HfxResponseInterface {

	 public void onHfxUpdate(int type, ByteBuffer byteBuffer);
}
