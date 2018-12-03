package com.mojang.minecraft.player;

import com.mojang.minecraft.SessionData;
import com.mojang.minecraft.level.tile.Block;
import java.io.Serializable;

public class Inventory implements Serializable {

   public static final long serialVersionUID = 0L;
   public static final int POP_TIME_DURATION = 5;
   public int[] slots = new int[9];
   public int[] count = new int[9];
   public int[] popTime = new int[9];
   public int selected = 0;


   public Inventory() {
      for(int i = 0; i < 9; ++i) {
         this.slots[i] = -1;
         this.count[i] = 0;
      }
   }

   public int getSelected() {
      return this.slots[this.selected];
   }

   private int getSlot(int resource) {
      for(int i = 0; i < this.slots.length; ++i) {
         if(resource == this.slots[i]) {
            return i;
         }
      }

      return -1;
   }

   public void grabTexture(int block, boolean isCreativeMode) {
      int var3;
      if((var3 = this.getSlot(block)) >= 0) {
         this.selected = var3;
      } else {
         if(isCreativeMode && block > 0 && SessionData.allowedBlocks.contains(Block.blocks[block])) {
            this.replaceSlot(Block.blocks[block]);
         }

      }
   }

   public void swapPaint(int var1) {
      if(var1 > 0) {
         var1 = 1;
      }

      if(var1 < 0) {
         var1 = -1;
      }

      for(this.selected -= var1; this.selected < 0; this.selected += this.slots.length) {
         ;
      }

      while(this.selected >= this.slots.length) {
         this.selected -= this.slots.length;
      }

   }

   public void replaceSlot(int var1) {
      if(var1 >= 0) {
         this.replaceSlot((Block)SessionData.allowedBlocks.get(var1));
      }

   }

   public void replaceSlot(Block var1) {
      if(var1 != null) {
         int var2;
         if((var2 = this.getSlot(var1.id)) >= 0) {
            this.slots[var2] = this.slots[this.selected];
         }

         this.slots[this.selected] = var1.id;
      }

   }

   public boolean addResource(int resource) {
      int slot;
      if((slot = this.getSlot(resource)) < 0) {
         slot = this.getSlot(-1);
      }

      if(slot < 0) {
         return false;
      } else if(this.count[slot] >= 99) {
         return false;
      } else {
         this.slots[slot] = resource;
         ++this.count[slot];
         this.popTime[slot] = 5;
         return true;
      }
   }

   public void tick() {
      for(int i = 0; i < this.popTime.length; ++i) {
         if(this.popTime[i] > 0) {
            --this.popTime[i];
         }
      }

   }

   public boolean removeResource(int resource) {
      if((resource = this.getSlot(resource)) < 0) {
         return false;
      } else {
         if(--this.count[resource] <= 0) {
            this.slots[resource] = -1;
         }

         return true;
      }
   }
}
