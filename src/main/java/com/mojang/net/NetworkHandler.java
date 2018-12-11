package com.mojang.net;

import com.mojang.minecraft.net.NetworkManager;
import com.mojang.minecraft.net.PacketType;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

public final class NetworkHandler {

   public volatile boolean connected;
   public SocketChannel channel;
   public ByteBuffer in = ByteBuffer.allocate(1048576);
   public ByteBuffer out = ByteBuffer.allocate(1048576);
   public NetworkManager netManager;
   private Socket sock;
   private boolean unused = false;
   private byte[] stringBytes = new byte[64];


   public NetworkHandler(String var1, int var2) {
	   try
	   {
		   channel = SocketChannel.open();
		   this.channel.connect(new InetSocketAddress(var1, var2));
		   this.channel.configureBlocking(false);
		   System.currentTimeMillis();
		   this.sock = this.channel.socket();
		   this.connected = true;
		   this.in.clear();
		   this.out.clear();
		   this.sock.setTcpNoDelay(true);
		   this.sock.setTrafficClass(24);
		   this.sock.setKeepAlive(false);
		   this.sock.setReuseAddress(false);
		   this.sock.setSoTimeout(100);
		   this.sock.getInetAddress().toString();
	   } catch (SocketException e) {
		   e.printStackTrace();
	   } catch (IOException e) {
		   e.printStackTrace();
	   }
   }

   public final void close() {
      try {
         if(this.out.position() > 0) {
            this.out.flip();
            this.channel.write(this.out);
            this.out.compact();
         }
      } catch (Exception var2) {
         ;
      }

      this.connected = false;

      try {
         this.channel.close();
      } catch (Exception var1) {
         ;
      }

      this.sock = null;
      this.channel = null;
   }

   public final void send(PacketType type, Object ... objects) {
      if(this.connected) {
         this.out.put(type.opcode);

         for(int i = 0; i < objects.length; ++i) {
            Object obj = objects[i];
            Class clss = type.params[i];
            NetworkHandler net = this;
            if(this.connected) {
               try {
                  if(clss == Long.TYPE) {
                     net.out.putLong(((Long)obj).longValue());
                  } else if(clss == Integer.TYPE) {
                     net.out.putInt(((Number)obj).intValue());
                  } else if(clss == Short.TYPE) {
                     net.out.putShort(((Number)obj).shortValue());
                  } else if(clss == Byte.TYPE) {
                     net.out.put(((Number)obj).byteValue());
                  } else if(clss == Double.TYPE) {
                     net.out.putDouble(((Double)obj).doubleValue());
                  } else if(clss == Float.TYPE) {
                     net.out.putFloat(((Float)obj).floatValue());
                  } else {
                     byte[] buff;
                     if(clss != String.class) {
                        if(clss == byte[].class) {
                           if((buff = (byte[])((byte[])obj)).length < 1024) {
                              buff = Arrays.copyOf(buff, 1024);
                           }

                           net.out.put(buff);
                        }
                     } else {
                        buff = ((String)obj).getBytes("UTF-8");
                        Arrays.fill(net.stringBytes, (byte)32);

                        int j;
                        for(j = 0; j < 64 && j < buff.length; ++j) {
                           net.stringBytes[j] = buff[j];
                        }

                        for(j = buff.length; j < 64; ++j) {
                           net.stringBytes[j] = 32;
                        }

                        net.out.put(net.stringBytes);
                     }
                  }
               } catch (Exception exception) {
                  this.netManager.error(exception);
               }
            }
         }

      }
   }

   public Object readObject(Class var1) {
      if(!this.connected) {
         return null;
      } else {
         try {
            if(var1 == Long.TYPE) {
               return Long.valueOf(this.in.getLong());
            } else if(var1 == Integer.TYPE) {
               return Integer.valueOf(this.in.getInt());
            } else if(var1 == Short.TYPE) {
               return Short.valueOf(this.in.getShort());
            } else if(var1 == Byte.TYPE) {
               return Byte.valueOf(this.in.get());
            } else if(var1 == Double.TYPE) {
               return Double.valueOf(this.in.getDouble());
            } else if(var1 == Float.TYPE) {
               return Float.valueOf(this.in.getFloat());
            } else if(var1 == String.class) {
               this.in.get(this.stringBytes);
               return (new String(this.stringBytes, "UTF-8")).trim();
            } else if(var1 == byte[].class) {
               byte[] var3 = new byte[1024];
               this.in.get(var3);
               return var3;
            } else {
               return null;
            }
         } catch (Exception var2) {
            this.netManager.error(var2);
            return null;
         }
      }
   }
}
