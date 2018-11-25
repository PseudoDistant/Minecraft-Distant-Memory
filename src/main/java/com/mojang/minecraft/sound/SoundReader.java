package com.mojang.minecraft.sound;

import de.jarnbjo.ogg.LogicalOggStreamImpl;
import de.jarnbjo.ogg.OggFormatException;
import de.jarnbjo.ogg.OnDemandUrlStream;
import de.jarnbjo.vorbis.IdentificationHeader;
import de.jarnbjo.vorbis.VorbisFormatException;
import de.jarnbjo.vorbis.VorbisStream;

import java.io.IOException;
import java.net.URL;

// TODO.
public final class SoundReader {

   public static SoundData read(URL url) {
	   VorbisStream vorbis = null;
	   try
	   {
		   LogicalOggStreamImpl ogg = (LogicalOggStreamImpl)(new OnDemandUrlStream(url)).getLogicalStreams().iterator().next();
		   vorbis = new VorbisStream(ogg);
	   } catch (VorbisFormatException e) {
		   e.printStackTrace();
	   } catch (OggFormatException e) {
		   e.printStackTrace();
	   } catch (IOException e) {
		   e.printStackTrace();
	   }
	   byte[] buffer = new byte[4096];
       int written = 0;
       boolean var1 = false;
       int channels = vorbis.getIdentificationHeader().getChannels();
       short[] data = new short[4096];
       int length = 0;

      label51:
      while(written >= 0) {
         int offset = 0;

         while(true) {
            try {
               if(offset < buffer.length && (written = vorbis.readPcm(buffer, offset, buffer.length - offset)) > 0) {
                  offset += written;
                  continue;
               }
            } catch (Exception exception) {
               written = -1;
            }
            if(offset <= 0) {
               break;
            }

            boolean var7 = false;
            int off = 0;

            while(true) {
               if(off >= offset) {
                  break label51;
               }

               int var8 = 0;

               for(int i = 0; i < channels; ++i) {
                  var8 += buffer[off++] << 8 | buffer[off++] & 255;
               }

               if(length == data.length) {
                  short[] shortData = data;
                  data = new short[data.length << 1];
                  System.arraycopy(shortData, 0, data, 0, length);
               }

               data[length++] = (short)(var8 / channels);
            }
         }
      }
      if(length != data.length) {
         short[] shortData = data;
         data = new short[length];
         System.arraycopy(shortData, 0, data, 0, length);
      }

      IdentificationHeader header = vorbis.getIdentificationHeader();
      return new SoundData(data, (float)header.getSampleRate());
   }
}
