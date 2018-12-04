package com.mojang.minecraft.level;

import com.mojang.minecraft.ProgressBarDisplay;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public final class LevelIO {

   private ProgressBarDisplay progressBar;


   public LevelIO(ProgressBarDisplay var1) {
      this.progressBar = var1;
   }

   public final boolean save(Level level, File saveFile) {
      try {
         FileOutputStream out = new FileOutputStream(saveFile);
         save(level, (OutputStream)out);
         out.close();
         return true;
      } catch (Exception exception) {
         exception.printStackTrace();
         if(this.progressBar != null) {
            this.progressBar.setText("Failed!");
         }

         try {
            Thread.sleep(1000L);
         } catch (InterruptedException interrupted) {
            ;
         }

         return false;
      }
   }

   public final Level load(File saveFile) {
      try {
         FileInputStream in = new FileInputStream(saveFile);
         Level var2 = this.load((InputStream)in);
         in.close();
         return var2;
      } catch (Exception exception) {
         exception.printStackTrace();
         if(this.progressBar != null) {
            this.progressBar.setText("Failed!");
         }

         try {
            Thread.sleep(1000L);
         } catch (InterruptedException interrupted) {
            ;
         }

         return null;
      }
   }

   public final boolean saveOnline(Level level, String host, String username, String sessionId, String levelName, int levelId) {
      if(sessionId == null) {
         sessionId = "";
      }

      if(this.progressBar != null && this.progressBar != null) {
         this.progressBar.setTitle("Saving level");
      }

      try {
         if(this.progressBar != null && this.progressBar != null) {
            this.progressBar.setText("Compressing..");
         }

         ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
         save(level, (OutputStream)byteArrayOut);
         byteArrayOut.close();
         byte[] byteArray = byteArrayOut.toByteArray();
         if(this.progressBar != null && this.progressBar != null) {
            this.progressBar.setText("Connecting..");
         }

         HttpURLConnection urlConnection;
         (urlConnection = (HttpURLConnection)(new URL("http://" + host + "/level/save.html")).openConnection()).setDoInput(true);
         urlConnection.setDoOutput(true);
         urlConnection.setRequestMethod("POST");
         DataOutputStream out;
         (out = new DataOutputStream(urlConnection.getOutputStream())).writeUTF(username);
         out.writeUTF(sessionId);
         out.writeUTF(levelName);
         out.writeByte(levelId);
         out.writeInt(byteArray.length);
         if(this.progressBar != null) {
            this.progressBar.setText("Saving..");
         }

         out.write(byteArray);
         out.close();
         BufferedReader in;
         if(!(in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()))).readLine().equalsIgnoreCase("ok")) {
            if(this.progressBar != null) {
               this.progressBar.setText("Failed: " + in.readLine());
            }

            in.close();
            Thread.sleep(1000L);
            return false;
         } else {
            in.close();
            return true;
         }
      } catch (Exception exception) {
         exception.printStackTrace();
         if(this.progressBar != null) {
            this.progressBar.setText("Failed!");
         }

         try {
            Thread.sleep(1000L);
         } catch (InterruptedException interrupted) {
            ;
         }

         return false;
      }
   }

   public final Level loadOnline(String server, String user, int id) {
      if(this.progressBar != null) {
         this.progressBar.setTitle("Loading level");
      }

      try {
         if(this.progressBar != null) {
            this.progressBar.setText("Connecting..");
         }

         HttpURLConnection urlConnection;
         (urlConnection = (HttpURLConnection)(new URL("http://" + server + "/level/load.html?id=" + id + "&user=" + user)).openConnection()).setDoInput(true);
         if(this.progressBar != null) {
            this.progressBar.setText("Loading..");
         }

         DataInputStream in;
         if((in = new DataInputStream(urlConnection.getInputStream())).readUTF().equalsIgnoreCase("ok")) {
            return this.load((InputStream)in);
         } else {
            if(this.progressBar != null) {
               this.progressBar.setText("Failed: " + in.readUTF());
            }

            in.close();
            Thread.sleep(1000L);
            return null;
         }
      } catch (Exception exception) {
         exception.printStackTrace();
         if(this.progressBar != null) {
            this.progressBar.setText("Failed!");
         }

         try {
            Thread.sleep(3000L);
         } catch (InterruptedException interrupted) {
            ;
         }

         return null;
      }
   }

   public final Level load(InputStream levelInputStream) {
      if(this.progressBar != null) {
         this.progressBar.setTitle("Loading level");
      }

      if(this.progressBar != null) {
         this.progressBar.setText("Reading..");
      }

      try {
         DataInputStream dataInputStream;
         if((dataInputStream = new DataInputStream(new GZIPInputStream(levelInputStream))).readInt() != 656127880) {
            return null;
         } else {
            byte version;
            if((version = dataInputStream.readByte()) > 2) {
               return null;
            } else if(version <= 1) {
               String levelName = dataInputStream.readUTF();
               String levelCreator = dataInputStream.readUTF();
               long levelCreationTime = dataInputStream.readLong();
               short width = dataInputStream.readShort();
               short height = dataInputStream.readShort();
               short depth = dataInputStream.readShort();
               byte[] blocks = new byte[width * height * depth];
               dataInputStream.readFully(blocks);
               dataInputStream.close();
               Level gameLevel;
               (gameLevel = new Level()).setData(width, depth, height, blocks);
               gameLevel.name = levelName;
               gameLevel.creator = levelCreator;
               gameLevel.createTime = levelCreationTime;
               return gameLevel;
            } else {
               Level level;
               LevelObjectInputStream in;
               (level = (Level)(in = new LevelObjectInputStream(dataInputStream)).readObject()).initTransient();
               in.close();
               return level;
            }
         }
      } catch (Exception exception) {
         exception.printStackTrace();
         System.out.println("Failed to load level: " + exception.toString());
         return null;
      }
   }

   public static void save(Level level, OutputStream outputStream) {
      try {
         DataOutputStream dataOutputStream;
         (dataOutputStream = new DataOutputStream(new GZIPOutputStream(outputStream))).writeInt(656127880);
         dataOutputStream.writeByte(2);
         ObjectOutputStream objectOutputStream;
         (objectOutputStream = new ObjectOutputStream(dataOutputStream)).writeObject(level);
         objectOutputStream.close();
      } catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   public static byte[] decompress(InputStream inputStream) {
      try {
         DataInputStream dataInputStream;
         byte[] byteArrayData = new byte[(dataInputStream = new DataInputStream(new GZIPInputStream(inputStream))).readInt()];
         dataInputStream.readFully(byteArrayData);
         dataInputStream.close();
         return byteArrayData;
      } catch (Exception exception) {
         throw new RuntimeException(exception);
      }
   }
}
