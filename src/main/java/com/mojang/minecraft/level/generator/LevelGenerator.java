package com.mojang.minecraft.level.generator;

import com.mojang.minecraft.ProgressBarDisplay;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.level.generator.noise.CombinedNoise;
import com.mojang.minecraft.level.generator.noise.OctaveNoise;
import com.mojang.minecraft.level.tile.Block;
import com.mojang.util.MathHelper;
import java.util.ArrayList;
import java.util.Random;

public final class LevelGenerator {

   private ProgressBarDisplay progressBar;
   private int width;
   private int depth;
   private int height;
   private Random random = new Random();
   private byte[] blocks;
   private int waterLevel;
   private int[] h = new int[1048576];


   public LevelGenerator(ProgressBarDisplay progress) {
      this.progressBar = progress;
   }

   public final Level generate(String username, int width, int depth, int var4) {
      this.progressBar.setTitle("Generating level");
      this.width = width;
      this.depth = depth;
      this.height = 64;
      this.waterLevel = 32;
      this.blocks = new byte[width * depth << 6];
      this.progressBar.setText("Raising..");
      LevelGenerator gen = this;
      CombinedNoise combinedNoise1 = new CombinedNoise(new OctaveNoise(this.random, 8), new OctaveNoise(this.random, 8));
      CombinedNoise combinedNoise2 = new CombinedNoise(new OctaveNoise(this.random, 8), new OctaveNoise(this.random, 8));
      OctaveNoise octaveNoise = new OctaveNoise(this.random, 6);
      int[] matrix = new int[this.width * this.depth];
      float var10 = 1.3F;

      int var11;
      int var12;
      for(var11 = 0; var11 < gen.width; ++var11) {
         gen.setProgress(var11 * 100 / (gen.width - 1));

         for(var12 = 0; var12 < gen.depth; ++var12) {
            double var13 = combinedNoise1.compute((double)((float)var11 * var10), (double)((float)var12 * var10)) / 6.0D + (double)-4;
            double var15 = combinedNoise2.compute((double)((float)var11 * var10), (double)((float)var12 * var10)) / 5.0D + 10.0D + (double)-4;
            if(octaveNoise.compute((double)var11, (double)var12) / 8.0D > 0.0D) {
               var15 = var13;
            }

            double var19;
            if((var19 = Math.max(var13, var15) / 2.0D) < 0.0D) {
               var19 *= 0.8D;
            }

            matrix[var11 + var12 * gen.width] = (int)var19;
         }
      }

      this.progressBar.setText("Eroding..");
      int[] var42 = matrix;
      gen = this;
      combinedNoise2 = new CombinedNoise(new OctaveNoise(this.random, 8), new OctaveNoise(this.random, 8));
      CombinedNoise var49 = new CombinedNoise(new OctaveNoise(this.random, 8), new OctaveNoise(this.random, 8));

      int var23;
      int var51;
      int var54;
      for(var51 = 0; var51 < gen.width; ++var51) {
         gen.setProgress(var51 * 100 / (gen.width - 1));

         for(var54 = 0; var54 < gen.depth; ++var54) {
            double var21 = combinedNoise2.compute((double)(var51 << 1), (double)(var54 << 1)) / 8.0D;
            var12 = var49.compute((double)(var51 << 1), (double)(var54 << 1)) > 0.0D?1:0;
            if(var21 > 2.0D) {
               var23 = ((var42[var51 + var54 * gen.width] - var12) / 2 << 1) + var12;
               var42[var51 + var54 * gen.width] = var23;
            }
         }
      }

      this.progressBar.setText("Soiling..");
      var42 = matrix;
      gen = this;
      int var46 = this.width;
      int var48 = this.depth;
      var51 = this.height;
      OctaveNoise var53 = new OctaveNoise(this.random, 8);

      int var25;
      int var24;
      int var27;
      int var26;
      int var28;
      for(var24 = 0; var24 < var46; ++var24) {
         gen.setProgress(var24 * 100 / (gen.width - 1));

         for(var11 = 0; var11 < var48; ++var11) {
            var12 = (int)(var53.compute((double)var24, (double)var11) / 24.0D) - 4;
            var25 = (var23 = var42[var24 + var11 * var46] + gen.waterLevel) + var12;
            var42[var24 + var11 * var46] = Math.max(var23, var25);
            if(var42[var24 + var11 * var46] > var51 - 2) {
               var42[var24 + var11 * var46] = var51 - 2;
            }

            if(var42[var24 + var11 * var46] < 1) {
               var42[var24 + var11 * var46] = 1;
            }

            for(var26 = 0; var26 < var51; ++var26) {
               var27 = (var26 * gen.depth + var11) * gen.width + var24;
               var28 = 0;
               if(var26 <= var23) {
                  var28 = Block.DIRT.id;
               }

               if(var26 <= var25) {
                  var28 = Block.STONE.id;
               }

               if(var26 == 0) {
                  var28 = Block.LAVA.id;
               }

               gen.blocks[var27] = (byte)var28;
            }
         }
      }

      this.progressBar.setText("Carving..");
      boolean var45 = true;
      boolean var44 = false;
      gen = this;
      var48 = this.width;
      var51 = this.depth;
      var54 = this.height;
      var24 = var48 * var51 * var54 / 256 / 64 << 1;

      for(var11 = 0; var11 < var24; ++var11) {
         gen.setProgress(var11 * 100 / (var24 - 1) / 4);
         float var55 = gen.random.nextFloat() * (float)var48;
         float var59 = gen.random.nextFloat() * (float)var54;
         float var56 = gen.random.nextFloat() * (float)var51;
         var26 = (int)((gen.random.nextFloat() + gen.random.nextFloat()) * 200.0F);
         float var61 = gen.random.nextFloat() * 3.1415927F * 2.0F;
         float var64 = 0.0F;
         float var29 = gen.random.nextFloat() * 3.1415927F * 2.0F;
         float var30 = 0.0F;
         float var31 = gen.random.nextFloat() * gen.random.nextFloat();

         for(int var32 = 0; var32 < var26; ++var32) {
            var55 += MathHelper.sin(var61) * MathHelper.cos(var29);
            var56 += MathHelper.cos(var61) * MathHelper.cos(var29);
            var59 += MathHelper.sin(var29);
            var61 += var64 * 0.2F;
            var64 = (var64 *= 0.9F) + (gen.random.nextFloat() - gen.random.nextFloat());
            var29 = (var29 + var30 * 0.5F) * 0.5F;
            var30 = (var30 *= 0.75F) + (gen.random.nextFloat() - gen.random.nextFloat());
            if(gen.random.nextFloat() >= 0.25F) {
               float var43 = var55 + (gen.random.nextFloat() * 4.0F - 2.0F) * 0.2F;
               float var50 = var59 + (gen.random.nextFloat() * 4.0F - 2.0F) * 0.2F;
               float var33 = var56 + (gen.random.nextFloat() * 4.0F - 2.0F) * 0.2F;
               float var34 = ((float)gen.height - var50) / (float)gen.height;
               var34 = 1.2F + (var34 * 3.5F + 1.0F) * var31;
               var34 = MathHelper.sin((float)var32 * 3.1415927F / (float)var26) * var34;

               for(int var35 = (int)(var43 - var34); var35 <= (int)(var43 + var34); ++var35) {
                  for(int var36 = (int)(var50 - var34); var36 <= (int)(var50 + var34); ++var36) {
                     for(int var37 = (int)(var33 - var34); var37 <= (int)(var33 + var34); ++var37) {
                        float var38 = (float)var35 - var43;
                        float var39 = (float)var36 - var50;
                        float var40 = (float)var37 - var33;
                        if(var38 * var38 + var39 * var39 * 2.0F + var40 * var40 < var34 * var34 && var35 >= 1 && var36 >= 1 && var37 >= 1 && var35 < gen.width - 1 && var36 < gen.height - 1 && var37 < gen.depth - 1) {
                           int var66 = (var36 * gen.depth + var37) * gen.width + var35;
                           if(gen.blocks[var66] == Block.STONE.id) {
                              gen.blocks[var66] = 0;
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      this.populateOre(Block.COAL_ORE.id, 90, 1, 4);
      this.populateOre(Block.IRON_ORE.id, 70, 2, 4);
      this.populateOre(Block.GOLD_ORE.id, 50, 3, 4);
      this.progressBar.setText("Watering..");
      gen = this;
      var51 = Block.STATIONARY_WATER.id;
      this.setProgress(0);

      for(var54 = 0; var54 < gen.width; ++var54) {
         gen.flood(var54, gen.height / 2 - 1, 0, 0, var51);
         gen.flood(var54, gen.height / 2 - 1, gen.depth - 1, 0, var51);
      }

      for(var54 = 0; var54 < gen.depth; ++var54) {
         gen.flood(0, gen.height / 2 - 1, var54, 0, var51);
         gen.flood(gen.width - 1, gen.height / 2 - 1, var54, 0, var51);
      }

      var54 = gen.width * gen.depth / 8000;

      for(var24 = 0; var24 < var54; ++var24) {
         if(var24 % 100 == 0) {
            gen.setProgress(var24 * 100 / (var54 - 1));
         }

         var11 = gen.random.nextInt(gen.width);
         var12 = gen.waterLevel - 1 - gen.random.nextInt(2);
         var23 = gen.random.nextInt(gen.depth);
         if(gen.blocks[(var12 * gen.depth + var23) * gen.width + var11] == 0) {
            gen.flood(var11, var12, var23, 0, var51);
         }
      }

      gen.setProgress(100);
      this.progressBar.setText("Melting..");
      gen = this;
      var46 = this.width * this.depth * this.height / 20000;

      for(var48 = 0; var48 < var46; ++var48) {
         if(var48 % 100 == 0) {
            gen.setProgress(var48 * 100 / (var46 - 1));
         }

         var51 = gen.random.nextInt(gen.width);
         var54 = (int)(gen.random.nextFloat() * gen.random.nextFloat() * (float)(gen.waterLevel - 3));
         var24 = gen.random.nextInt(gen.depth);
         if(gen.blocks[(var54 * gen.depth + var24) * gen.width + var51] == 0) {
            gen.flood(var51, var54, var24, 0, Block.STATIONARY_LAVA.id);
         }
      }

      gen.setProgress(100);
      this.progressBar.setText("Growing..");
      var42 = matrix;
      gen = this;
      var46 = this.width;
      var48 = this.depth;
      var51 = this.height;
      var53 = new OctaveNoise(this.random, 8);
      OctaveNoise var58 = new OctaveNoise(this.random, 8);

      int var63;
      for(var11 = 0; var11 < var46; ++var11) {
         gen.setProgress(var11 * 100 / (gen.width - 1));

         for(var12 = 0; var12 < var48; ++var12) {
            boolean var60 = var53.compute((double)var11, (double)var12) > 8.0D;
            boolean var57 = var58.compute((double)var11, (double)var12) > 12.0D;
            var27 = ((var26 = var42[var11 + var12 * var46]) * gen.depth + var12) * gen.width + var11;
            if(((var28 = gen.blocks[((var26 + 1) * gen.depth + var12) * gen.width + var11] & 255) == Block.WATER.id || var28 == Block.STATIONARY_WATER.id) && var26 <= var51 / 2 - 1 && var57) {
               gen.blocks[var27] = (byte)Block.GRAVEL.id;
            }

            if(var28 == 0) {
               var63 = Block.GRASS.id;
               if(var26 <= var51 / 2 - 1 && var60) {
                  var63 = Block.SAND.id;
               }

               gen.blocks[var27] = (byte)var63;
            }
         }
      }

      this.progressBar.setText("Planting..");
      var42 = matrix;
      gen = this;
      var46 = this.width;
      var48 = this.width * this.depth / 3000;

      for(var51 = 0; var51 < var48; ++var51) {
         var54 = gen.random.nextInt(2);
         gen.setProgress(var51 * 50 / (var48 - 1));
         var24 = gen.random.nextInt(gen.width);
         var11 = gen.random.nextInt(gen.depth);

         for(var12 = 0; var12 < 10; ++var12) {
            var23 = var24;
            var25 = var11;

            for(var26 = 0; var26 < 5; ++var26) {
               var23 += gen.random.nextInt(6) - gen.random.nextInt(6);
               var25 += gen.random.nextInt(6) - gen.random.nextInt(6);
               if((var54 < 2 || gen.random.nextInt(4) == 0) && var23 >= 0 && var25 >= 0 && var23 < gen.width && var25 < gen.depth) {
                  var27 = var42[var23 + var25 * var46] + 1;
                  if((gen.blocks[(var27 * gen.depth + var25) * gen.width + var23] & 255) == 0) {
                     var63 = (var27 * gen.depth + var25) * gen.width + var23;
                     if((gen.blocks[((var27 - 1) * gen.depth + var25) * gen.width + var23] & 255) == Block.GRASS.id) {
                        if(var54 == 0) {
                           gen.blocks[var63] = (byte)Block.DANDELION.id;
                        } else if(var54 == 1) {
                           gen.blocks[var63] = (byte)Block.ROSE.id;
                        }
                     }
                  }
               }
            }
         }
      }

      var42 = matrix;
      gen = this;
      var46 = this.width;
      var51 = this.width * this.depth * this.height / 2000;

      for(var54 = 0; var54 < var51; ++var54) {
         var24 = gen.random.nextInt(2);
         gen.setProgress(var54 * 50 / (var51 - 1) + 50);
         var11 = gen.random.nextInt(gen.width);
         var12 = gen.random.nextInt(gen.height);
         var23 = gen.random.nextInt(gen.depth);

         for(var25 = 0; var25 < 20; ++var25) {
            var26 = var11;
            var27 = var12;
            var28 = var23;

            for(var63 = 0; var63 < 5; ++var63) {
               var26 += gen.random.nextInt(6) - gen.random.nextInt(6);
               var27 += gen.random.nextInt(2) - gen.random.nextInt(2);
               var28 += gen.random.nextInt(6) - gen.random.nextInt(6);
               if((var24 < 2 || gen.random.nextInt(4) == 0) && var26 >= 0 && var28 >= 0 && var27 >= 1 && var26 < gen.width && var28 < gen.depth && var27 < var42[var26 + var28 * var46] - 1 && (gen.blocks[(var27 * gen.depth + var28) * gen.width + var26] & 255) == 0) {
                  int var62 = (var27 * gen.depth + var28) * gen.width + var26;
                  if((gen.blocks[((var27 - 1) * gen.depth + var28) * gen.width + var26] & 255) == Block.STONE.id) {
                     if(var24 == 0) {
                        gen.blocks[var62] = (byte)Block.BROWN_MUSHROOM.id;
                     } else if(var24 == 1) {
                        gen.blocks[var62] = (byte)Block.RED_MUSHROOM.id;
                     }
                  }
               }
            }
         }
      }

      Level var65;
      (var65 = new Level()).waterLevel = this.waterLevel;
      var65.setData(width, 64, depth, this.blocks);
      var65.createTime = System.currentTimeMillis();
      var65.creator = username;
      var65.name = "A Nice World";
      int[] var52 = matrix;
      Level var47 = var65;
      gen = this;
      var48 = this.width;
      var51 = this.width * this.depth / 4000;

      for(var54 = 0; var54 < var51; ++var54) {
         gen.setProgress(var54 * 50 / (var51 - 1) + 50);
         var24 = gen.random.nextInt(gen.width);
         var11 = gen.random.nextInt(gen.depth);

         for(var12 = 0; var12 < 20; ++var12) {
            var23 = var24;
            var25 = var11;

            for(var26 = 0; var26 < 20; ++var26) {
               var23 += gen.random.nextInt(6) - gen.random.nextInt(6);
               var25 += gen.random.nextInt(6) - gen.random.nextInt(6);
               if(var23 >= 0 && var25 >= 0 && var23 < gen.width && var25 < gen.depth) {
                  var27 = var52[var23 + var25 * var48] + 1;
                  if(gen.random.nextInt(4) == 0) {
                     var47.maybeGrowTree(var23, var27, var25);
                  }
               }
            }
         }
      }

      return var65;
   }

   private void populateOre(int var1, int var2, int var3, int var4) {
      byte var25 = (byte)var1;
      var4 = this.width;
      int var5 = this.depth;
      int var6 = this.height;
      int var7 = var4 * var5 * var6 / 256 / 64 * var2 / 100;

      for(int var8 = 0; var8 < var7; ++var8) {
         this.setProgress(var8 * 100 / (var7 - 1) / 4 + var3 * 100 / 4);
         float var9 = this.random.nextFloat() * (float)var4;
         float var10 = this.random.nextFloat() * (float)var6;
         float var11 = this.random.nextFloat() * (float)var5;
         int var12 = (int)((this.random.nextFloat() + this.random.nextFloat()) * 75.0F * (float)var2 / 100.0F);
         float var13 = this.random.nextFloat() * 3.1415927F * 2.0F;
         float var14 = 0.0F;
         float var15 = this.random.nextFloat() * 3.1415927F * 2.0F;
         float var16 = 0.0F;

         for(int var17 = 0; var17 < var12; ++var17) {
            var9 += MathHelper.sin(var13) * MathHelper.cos(var15);
            var11 += MathHelper.cos(var13) * MathHelper.cos(var15);
            var10 += MathHelper.sin(var15);
            var13 += var14 * 0.2F;
            var14 = (var14 *= 0.9F) + (this.random.nextFloat() - this.random.nextFloat());
            var15 = (var15 + var16 * 0.5F) * 0.5F;
            var16 = (var16 *= 0.9F) + (this.random.nextFloat() - this.random.nextFloat());
            float var18 = MathHelper.sin((float)var17 * 3.1415927F / (float)var12) * (float)var2 / 100.0F + 1.0F;

            for(int var19 = (int)(var9 - var18); var19 <= (int)(var9 + var18); ++var19) {
               for(int var20 = (int)(var10 - var18); var20 <= (int)(var10 + var18); ++var20) {
                  for(int var21 = (int)(var11 - var18); var21 <= (int)(var11 + var18); ++var21) {
                     float var22 = (float)var19 - var9;
                     float var23 = (float)var20 - var10;
                     float var24 = (float)var21 - var11;
                     if(var22 * var22 + var23 * var23 * 2.0F + var24 * var24 < var18 * var18 && var19 >= 1 && var20 >= 1 && var21 >= 1 && var19 < this.width - 1 && var20 < this.height - 1 && var21 < this.depth - 1) {
                        int var26 = (var20 * this.depth + var21) * this.width + var19;
                        if(this.blocks[var26] == Block.STONE.id) {
                           this.blocks[var26] = var25;
                        }
                     }
                  }
               }
            }
         }
      }

   }

   private void setProgress(int progress) {
      this.progressBar.setProgress(progress);
   }

   private long flood(int var1, int var2, int var3, int var4, int var5) {
      byte var20 = (byte)var5;
      ArrayList var21 = new ArrayList();
      byte var6 = 0;
      int var7 = 1;

      int var8;
      for(var8 = 1; 1 << var7 < this.width; ++var7) {
         ;
      }

      while(1 << var8 < this.depth) {
         ++var8;
      }

      int var9 = this.depth - 1;
      int var10 = this.width - 1;
      int var22 = var6 + 1;
      this.h[0] = ((var2 << var8) + var3 << var7) + var1;
      long var11 = 0L;
      var1 = this.width * this.depth;

      while(var22 > 0) {
         --var22;
         var2 = this.h[var22];
         if(var22 == 0 && var21.size() > 0) {
            this.h = (int[])var21.remove(var21.size() - 1);
            var22 = this.h.length;
         }

         var3 = var2 >> var7 & var9;
         int var13 = var2 >> var7 + var8;

         int var14;
         int var15;
         for(var15 = var14 = var2 & var10; var14 > 0 && this.blocks[var2 - 1] == 0; --var2) {
            --var14;
         }

         while(var15 < this.width && this.blocks[var2 + var15 - var14] == 0) {
            ++var15;
         }

         int var16 = var2 >> var7 & var9;
         int var17 = var2 >> var7 + var8;
         if(var16 != var3 || var17 != var13) {
            System.out.println("Diagonal flood!?");
         }

         boolean var23 = false;
         boolean var24 = false;
         boolean var18 = false;
         var11 += (long)(var15 - var14);

         for(var14 = var14; var14 < var15; ++var14) {
            this.blocks[var2] = var20;
            boolean var19;
            if(var3 > 0) {
               if((var19 = this.blocks[var2 - this.width] == 0) && !var23) {
                  if(var22 == this.h.length) {
                     var21.add(this.h);
                     this.h = new int[1048576];
                     var22 = 0;
                  }

                  this.h[var22++] = var2 - this.width;
               }

               var23 = var19;
            }

            if(var3 < this.depth - 1) {
               if((var19 = this.blocks[var2 + this.width] == 0) && !var24) {
                  if(var22 == this.h.length) {
                     var21.add(this.h);
                     this.h = new int[1048576];
                     var22 = 0;
                  }

                  this.h[var22++] = var2 + this.width;
               }

               var24 = var19;
            }

            if(var13 > 0) {
               byte var25 = this.blocks[var2 - var1];
               if((var20 == Block.LAVA.id || var20 == Block.STATIONARY_LAVA.id) && (var25 == Block.WATER.id || var25 == Block.STATIONARY_WATER.id)) {
                  this.blocks[var2 - var1] = (byte)Block.STONE.id;
               }

               if((var19 = var25 == 0) && !var18) {
                  if(var22 == this.h.length) {
                     var21.add(this.h);
                     this.h = new int[1048576];
                     var22 = 0;
                  }

                  this.h[var22++] = var2 - var1;
               }

               var18 = var19;
            }

            ++var2;
         }
      }

      return var11;
   }
}
