package com.mojang.minecraft.sound;

import de.jarnbjo.ogg.EndOfOggStreamException;
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
            if (this.music.stopped) {
               return;
            }
            ByteBuffer buffer;
            if (this.music.playing == null) {
               if (this.music.current != null) {
                  this.music.playing = this.music.current;
                  this.music.current = null;
                  this.music.playing.clear();
               }
            }

            if (this.music.playing != null) {
               if (this.music.playing.remaining() != 0) {
                  while (true) {
                     if (this.music.playing.remaining() == 0) {
                        break;
                     }

                     buffer = this.music.playing;
                     int bytesRead = this.music.stream.readPcm(buffer.array(), buffer.position(), buffer.remaining());
                     buffer.position(buffer.position() + bytesRead);
                     if (bytesRead <= 0) {
                        this.music.finished = true;
                        this.music.stopped = true;
                        break;
                     }
                  }
               }
            }

            if (this.music.playing != null) {
               if (this.music.previous == null) {
                  this.music.playing.flip();
                  this.music.previous = this.music.playing;
                  this.music.playing = null;
               }
            }

            Thread.sleep(10L);
         } while (this.music.player.running);

         return;
      } catch (EndOfOggStreamException ignored) {
         // Do nothing.
         // Music finished.
      } catch (Exception exception) {
         exception.printStackTrace();
         return;
      } finally {
         this.music.finished = true;
      }

   }
}
