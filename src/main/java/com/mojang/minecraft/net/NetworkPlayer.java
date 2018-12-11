package com.mojang.minecraft.net;

import com.mojang.minecraft.Minecraft;
import com.mojang.minecraft.gui.FontRenderer;
import com.mojang.minecraft.mob.HumanoidMob;
import com.mojang.minecraft.net.PositionUpdate;
import com.mojang.minecraft.net.SkinDownloadThread;
import com.mojang.minecraft.render.TextureManager;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import org.lwjgl.opengl.GL11;

public class NetworkPlayer extends HumanoidMob {

   public static final long serialVersionUID = 77479605454997290L;
   private List moveQueue = new LinkedList();
   private Minecraft minecraft;
   private int xp;
   private int yp;
   private int zp;
   private transient int a = -1;
   public transient BufferedImage newTexture = null;
   public String name;
   public String displayName;
   int tickCount = 0;
   private TextureManager textures;


   public NetworkPlayer(Minecraft mc, int var2, String name, int xp, int yp, int zp, float yRot, float xRot) {
      super(mc.level, (float)xp, (float)yp, (float)zp);
      this.minecraft = mc;
      this.displayName = name;
      name = FontRenderer.stripColor(name);
      this.name = name;
      this.xp = xp;
      this.yp = yp;
      this.zp = zp;
      this.heightOffset = 0.0F;
      this.pushthrough = 0.8F;
      this.setPos((float)xp / 32.0F, (float)yp / 32.0F, (float)zp / 32.0F);
      this.xRot = xRot;
      this.yRot = yRot;
      this.armor = this.helmet = false;
      this.renderOffset = 0.6875F;
      (new SkinDownloadThread(this)).start();
      this.allowAlpha = false;
   }

   public void aiStep() {
      int iterations = 5;

      do {
         if(this.moveQueue.size() > 0) {
            this.setPos((PositionUpdate)this.moveQueue.remove(0));
         }
      } while(iterations-- > 0 && this.moveQueue.size() > 10);

      this.onGround = true;
   }

   public void bindTexture(TextureManager textureManager) {
      this.textures = textureManager;
      if(this.newTexture != null) {
         BufferedImage image = this.newTexture;
         int[] buff512 = new int[512];
         image.getRGB(32, 0, 32, 16, buff512, 0, 32);
         int iterations = 0;

         boolean hairAvailable;
         while(true) {
            if(iterations >= buff512.length) {
               hairAvailable = false;
               break;
            }

            if(buff512[iterations] >>> 24 < 128) {
               hairAvailable = true;
               break;
            }

            ++iterations;
         }

         this.hasHair = hairAvailable;
         this.a = textureManager.load(this.newTexture);
         this.newTexture = null;
      }

      if(this.a < 0) {
         GL11.glBindTexture(3553, textureManager.load("/char.png"));
      } else {
         GL11.glBindTexture(3553, this.a);
      }
   }

   public void renderHover(TextureManager textureManager, float var2) {
      FontRenderer reader = this.minecraft.fontRenderer;
      GL11.glPushMatrix();
      GL11.glTranslatef(this.xo + (this.x - this.xo) * var2, this.yo + (this.y - this.yo) * var2 + 0.8F + this.renderOffset, this.zo + (this.z - this.zo) * var2);
      GL11.glRotatef(-this.minecraft.player.yRot, 0.0F, 1.0F, 0.0F);
      var2 = 0.05F;
      GL11.glScalef(0.05F, -var2, var2);
      GL11.glTranslatef((float)(-reader.getWidth(this.displayName)) / 2.0F, 0.0F, 0.0F);
      GL11.glNormal3f(1.0F, -1.0F, 1.0F);
      GL11.glDisable(2896);
      GL11.glDisable(16384);
      if(this.name.equalsIgnoreCase("Notch")) {
         reader.renderNoShadow(this.displayName, 0, 0, 16776960);
      } else {
         reader.renderNoShadow(this.displayName, 0, 0, 16777215);
      }

      GL11.glDepthFunc(516);
      GL11.glDepthMask(false);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.8F);
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      reader.renderNoShadow(this.displayName, 0, 0, 16777215);
      GL11.glDisable(3042);
      GL11.glDepthMask(true);
      GL11.glDepthFunc(515);
      GL11.glTranslatef(1.0F, 1.0F, -0.05F);
      reader.renderNoShadow(this.name, 0, 0, 5263440);
      GL11.glEnable(16384);
      GL11.glEnable(2896);
      GL11.glPopMatrix();
   }

   public void queue(byte xp, byte yp, byte zp, float var4, float var5) {
      float var6 = var4 - this.yRot;

      float var7;
      for(var7 = var5 - this.xRot; var6 >= 180.0F; var6 -= 360.0F) {
         ;
      }

      while(var6 < -180.0F) {
         var6 += 360.0F;
      }

      while(var7 >= 180.0F) {
         var7 -= 360.0F;
      }

      while(var7 < -180.0F) {
         var7 += 360.0F;
      }

      var6 = this.yRot + var6 * 0.5F;
      var7 = this.xRot + var7 * 0.5F;
      this.moveQueue.add(new PositionUpdate(((float)this.xp + (float)xp / 2.0F) / 32.0F, ((float)this.yp + (float)yp / 2.0F) / 32.0F, ((float)this.zp + (float)zp / 2.0F) / 32.0F, var6, var7));
      this.xp += xp;
      this.yp += yp;
      this.zp += zp;
      this.moveQueue.add(new PositionUpdate((float)this.xp / 32.0F, (float)this.yp / 32.0F, (float)this.zp / 32.0F, var4, var5));
   }

   public void teleport(short xp, short yp, short zp, float var4, float var5) {
      float var6 = var4 - this.yRot;

      float var7;
      for(var7 = var5 - this.xRot; var6 >= 180.0F; var6 -= 360.0F) {
         ;
      }

      while(var6 < -180.0F) {
         var6 += 360.0F;
      }

      while(var7 >= 180.0F) {
         var7 -= 360.0F;
      }

      while(var7 < -180.0F) {
         var7 += 360.0F;
      }

      var6 = this.yRot + var6 * 0.5F;
      var7 = this.xRot + var7 * 0.5F;
      this.moveQueue.add(new PositionUpdate((float)(this.xp + xp) / 64.0F, (float)(this.yp + yp) / 64.0F, (float)(this.zp + zp) / 64.0F, var6, var7));
      this.xp = xp;
      this.yp = yp;
      this.zp = zp;
      this.moveQueue.add(new PositionUpdate((float)this.xp / 32.0F, (float)this.yp / 32.0F, (float)this.zp / 32.0F, var4, var5));
   }

   public void queue(byte xp, byte yp, byte zp) {
      this.moveQueue.add(new PositionUpdate(((float)this.xp + (float)xp / 2.0F) / 32.0F, ((float)this.yp + (float)yp / 2.0F) / 32.0F, ((float)this.zp + (float)zp / 2.0F) / 32.0F));
      this.xp += xp;
      this.yp += yp;
      this.zp += zp;
      this.moveQueue.add(new PositionUpdate((float)this.xp / 32.0F, (float)this.yp / 32.0F, (float)this.zp / 32.0F));
   }

   public void queue(float yawOrig, float pitchOrig) {
      float yawNew = yawOrig - this.yRot;

      float pitchNew;
      for(pitchNew = pitchOrig - this.xRot; yawNew >= 180.0F; yawNew -= 360.0F) {
         ;
      }

      while(yawNew < -180.0F) {
         yawNew += 360.0F;
      }

      while(pitchNew >= 180.0F) {
         pitchNew -= 360.0F;
      }

      while(pitchNew < -180.0F) {
         pitchNew += 360.0F;
      }

      yawNew = this.yRot + yawNew * 0.5F;
      pitchNew = this.xRot + pitchNew * 0.5F;
      this.moveQueue.add(new PositionUpdate(yawNew, pitchNew));
      this.moveQueue.add(new PositionUpdate(yawOrig, pitchOrig));
   }

   public void clear() {
      if(this.a >= 0 && this.textures != null) {
         this.textures.textureImages.remove(Integer.valueOf(this.a));
         this.textures.idBuffer.clear();
         this.textures.idBuffer.put(this.a);
         this.textures.idBuffer.flip();
         GL11.glDeleteTextures(this.textures.idBuffer);
      }

   }
}
