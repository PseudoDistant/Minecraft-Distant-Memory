package com.mojang.util;


public final class MathHelper {

   private static float[] SIN_TABLE = new float[65536];


   public static final float sin(float value) {
      return SIN_TABLE[(int)(value * 10430.378F) & '\uffff'];
   }

   public static final float cos(float value) {
      return SIN_TABLE[(int)(value * 10430.378F + 16384.0F) & '\uffff'];
   }

   public static final float sqrt(float value) {
      return (float)Math.sqrt((double)value);
   }

   static {
      for(int i = 0; i < 65536; ++i) {
         SIN_TABLE[i] = (float)Math.sin((double)i * 3.141592653589793D * 2.0D / 65536.0D);
      }

   }
}
