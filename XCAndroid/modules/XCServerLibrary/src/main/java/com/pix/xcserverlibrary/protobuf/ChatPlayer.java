// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: xc_protoc.proto at 43:1
package com.pix.xcserverlibrary.protobuf;

import com.squareup.wire.FieldEncoding;
import com.squareup.wire.Message;
import com.squareup.wire.ProtoAdapter;
import com.squareup.wire.ProtoReader;
import com.squareup.wire.ProtoWriter;
import com.squareup.wire.WireField;
import com.squareup.wire.internal.Internal;
import java.io.IOException;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import okio.ByteString;

/**
 * 场景玩家信息
 */
public final class ChatPlayer extends Message<ChatPlayer, ChatPlayer.Builder> {
  public static final ProtoAdapter<ChatPlayer> ADAPTER = new ProtoAdapter_ChatPlayer();

  private static final long serialVersionUID = 0L;

  public static final Long DEFAULT_ID = 0L;

  public static final String DEFAULT_NAME = "";

  public static final String DEFAULT_HEADIMG = "";

  @WireField(
      tag = 1,
      adapter = "com.squareup.wire.ProtoAdapter#SINT64"
  )
  public final Long id;

  @WireField(
      tag = 2,
      adapter = "com.squareup.wire.ProtoAdapter#STRING"
  )
  public final String name;

  @WireField(
      tag = 3,
      adapter = "com.squareup.wire.ProtoAdapter#STRING"
  )
  public final String headImg;

  public ChatPlayer(Long id, String name, String headImg) {
    this(id, name, headImg, ByteString.EMPTY);
  }

  public ChatPlayer(Long id, String name, String headImg, ByteString unknownFields) {
    super(ADAPTER, unknownFields);
    this.id = id;
    this.name = name;
    this.headImg = headImg;
  }

  @Override
  public Builder newBuilder() {
    Builder builder = new Builder();
    builder.id = id;
    builder.name = name;
    builder.headImg = headImg;
    builder.addUnknownFields(unknownFields());
    return builder;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof ChatPlayer)) return false;
    ChatPlayer o = (ChatPlayer) other;
    return Internal.equals(unknownFields(), o.unknownFields())
        && Internal.equals(id, o.id)
        && Internal.equals(name, o.name)
        && Internal.equals(headImg, o.headImg);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode;
    if (result == 0) {
      result = unknownFields().hashCode();
      result = result * 37 + (id != null ? id.hashCode() : 0);
      result = result * 37 + (name != null ? name.hashCode() : 0);
      result = result * 37 + (headImg != null ? headImg.hashCode() : 0);
      super.hashCode = result;
    }
    return result;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    if (id != null) builder.append(", id=").append(id);
    if (name != null) builder.append(", name=").append(name);
    if (headImg != null) builder.append(", headImg=").append(headImg);
    return builder.replace(0, 2, "ChatPlayer{").append('}').toString();
  }

  public static final class Builder extends Message.Builder<ChatPlayer, Builder> {
    public Long id;

    public String name;

    public String headImg;

    public Builder() {
    }

    public Builder id(Long id) {
      this.id = id;
      return this;
    }

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder headImg(String headImg) {
      this.headImg = headImg;
      return this;
    }

    @Override
    public ChatPlayer build() {
      return new ChatPlayer(id, name, headImg, buildUnknownFields());
    }
  }

  private static final class ProtoAdapter_ChatPlayer extends ProtoAdapter<ChatPlayer> {
    ProtoAdapter_ChatPlayer() {
      super(FieldEncoding.LENGTH_DELIMITED, ChatPlayer.class);
    }

    @Override
    public int encodedSize(ChatPlayer value) {
      return (value.id != null ? ProtoAdapter.SINT64.encodedSizeWithTag(1, value.id) : 0)
          + (value.name != null ? ProtoAdapter.STRING.encodedSizeWithTag(2, value.name) : 0)
          + (value.headImg != null ? ProtoAdapter.STRING.encodedSizeWithTag(3, value.headImg) : 0)
          + value.unknownFields().size();
    }

    @Override
    public void encode(ProtoWriter writer, ChatPlayer value) throws IOException {
      if (value.id != null) ProtoAdapter.SINT64.encodeWithTag(writer, 1, value.id);
      if (value.name != null) ProtoAdapter.STRING.encodeWithTag(writer, 2, value.name);
      if (value.headImg != null) ProtoAdapter.STRING.encodeWithTag(writer, 3, value.headImg);
      writer.writeBytes(value.unknownFields());
    }

    @Override
    public ChatPlayer decode(ProtoReader reader) throws IOException {
      Builder builder = new Builder();
      long token = reader.beginMessage();
      for (int tag; (tag = reader.nextTag()) != -1;) {
        switch (tag) {
          case 1: builder.id(ProtoAdapter.SINT64.decode(reader)); break;
          case 2: builder.name(ProtoAdapter.STRING.decode(reader)); break;
          case 3: builder.headImg(ProtoAdapter.STRING.decode(reader)); break;
          default: {
            FieldEncoding fieldEncoding = reader.peekFieldEncoding();
            Object value = fieldEncoding.rawProtoAdapter().decode(reader);
            builder.addUnknownField(tag, fieldEncoding, value);
          }
        }
      }
      reader.endMessage(token);
      return builder.build();
    }

    @Override
    public ChatPlayer redact(ChatPlayer value) {
      Builder builder = value.newBuilder();
      builder.clearUnknownFields();
      return builder.build();
    }
  }
}
