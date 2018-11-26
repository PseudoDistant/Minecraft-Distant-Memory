package com.mojang.minecraft.sound;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;

// TODO.
public final class SoundManager {

   private SoundReader reader = new SoundReader();
   public Map sounds = new HashMap();
   private Map music = new HashMap();
   public Random random = new Random();
   public long lastMusic = System.currentTimeMillis() + 60000L;


   public final AudioInfo getAudioInfo(String var1, float var2, float var3) {
      List var4 = null;
      Map var5 = this.sounds;
      synchronized(this.sounds) {
         var4 = (List)this.sounds.get(var1);
      }

      if(var4 == null) {
         return null;
      } else {
         SoundData var7 = (SoundData)var4.get(this.random.nextInt(var4.size()));
         return new SoundInfo(var7, var3, var2);
      }
   }

   public void registerSound(File file, String name) {
      try {
         for(name = name.substring(0, name.length() - 4).replaceAll("/", "."); Character.isDigit(name.charAt(name.length() - 1)); name = name.substring(0, name.length() - 1)) {
            ;
         }
         SoundData data = SoundReader.read(file.toURI().toURL());
         synchronized(this.sounds) {
            List list;
            if((list = (List)this.sounds.get(name)) == null) {
               list = new ArrayList();
               this.sounds.put(name, list);
            }
            ((List)list).add(data);
         }
      } catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   public final void registerMusic(String name, File file) {
      synchronized(this.music) {
         for(name = name.substring(0, name.length() - 4).replaceAll("/", "."); Character.isDigit(name.charAt(name.length() - 1)); name = name.substring(0, name.length() - 1)) {
            ;
         }

         Object list;
         if((list = (List)this.music.get(name)) == null) {
            list = new ArrayList();
            this.music.put(name, list);
         }

         ((List)list).add(file);
      }
   }

   public boolean playMusic(SoundPlayer player, String name) {
      List list = null;
      synchronized(this.music) {
         list = (List)this.music.get(name);
      }

      if(list == null) {
         return false;
      } else {
         File file = (File)list.get(this.random.nextInt(list.size()));

         try {
            player.play(new Music(player, file.toURI().toURL()));
         } catch (IOException ioException) {
            ioException.printStackTrace();
         }

         return true;
      }
   }
}
