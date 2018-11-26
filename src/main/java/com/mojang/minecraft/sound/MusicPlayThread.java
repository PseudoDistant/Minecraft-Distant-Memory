package com.mojang.minecraft.sound;

import de.jarnbjo.vorbis.VorbisStream;

import java.nio.ByteBuffer;

// TODO.
final class MusicPlayThread extends Thread {

   // $FF: synthetic field
   private Music music;


   public MusicPlayThread(Music music) {
	   super();
      this.music = music;
      this.setPriority(10);
      this.setDaemon(true);
   }

   public final void run() {
      try {
         do {
            if(this.music.stopped) {
               return;
            }

            Music musica = this.music;
            ByteBuffer buffer;
            Music var10001;
            if(this.music.playing == null) {
               musica = this.music;
               if(this.music.current != null) {
                  musica = this.music;
                  buffer = this.music.current;
                  var10001 = this.music;
                  this.music.playing = buffer;
                  buffer = null;
                  musica = this.music;
                  this.music.current = null;
                  musica = this.music;
                  this.music.playing.clear();
               }
            }

            musica = this.music;
            if(this.music.playing != null) {
               musica = this.music;
               if(this.music.playing.remaining() != 0) {
                  while(true) {
                     musica = this.music;
                     if(this.music.playing.remaining() == 0) {
                        break;
                     }

                     musica = this.music;
                     musica = this.music;
                     buffer = this.music.playing;
                     VorbisStream var9 = this.music.stream;
                     int var10 = this.music.stream.readPcm(buffer.array(), buffer.position(), buffer.remaining());
                     buffer.position(buffer.position() + var10);
                     boolean var11;
                     if(var11 = var10 <= 0) {
                        this.music.finished = true;
                        this.music.stopped = true;
                        break;
                     }
                  }
               }
            }

            musica = this.music;
            if(this.music.playing != null) {
               musica = this.music;
               if(this.music.previous == null) {
                  musica = this.music;
                  this.music.playing.flip();
                  musica = this.music;
                  buffer = this.music.playing;
                  var10001 = this.music;
                  this.music.previous = buffer;
                  buffer = null;
                  musica = this.music;
                  this.music.playing = buffer;
               }
            }

            Thread.sleep(10L);
            musica = this.music;
         } while(this.music.player.running);

         return;
      } catch (Exception exception) {
         exception.printStackTrace();
         return;
      } finally {
         this.music.finished = true;
      }

   }
}
