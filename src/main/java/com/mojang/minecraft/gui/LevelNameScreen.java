package com.mojang.minecraft.gui;

import com.mojang.minecraft.Minecraft;
import com.mojang.minecraft.gui.Button;
import com.mojang.minecraft.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

public final class LevelNameScreen extends GuiScreen {

   private GuiScreen parent;
   private String title = "Enter level name:";
   private int id;
   private String name;
   private int counter = 0;

   private static final int BTN_SAVE   = 0;
   private static final int BTN_CANCEL = 1;


   public LevelNameScreen(GuiScreen screen, String name, int id) {
      this.parent = screen;
      this.id = id;
      this.name = name;
      if(this.name.equals("-")) {
         this.name = "";
      }

   }

   public final void onOpen() {
      this.buttons.clear();
      Keyboard.enableRepeatEvents(true);
      this.buttons.add(new Button(BTN_SAVE,   this.width / 2 - 100, this.height / 4 + 120, "Save"));
      this.buttons.add(new Button(BTN_CANCEL, this.width / 2 - 100, this.height / 4 + 144, "Cancel"));
      ((Button)this.buttons.get(BTN_SAVE)).active = this.name.trim().length() > 1;
   }

   public final void onClose() {
      Keyboard.enableRepeatEvents(false);
   }

   public final void tick() {
      ++this.counter;
   }

   protected final void onButtonClick(Button button) {
      if(button.active) {
         if(button.id == BTN_SAVE && this.name.trim().length() > 1) {
            Minecraft var10000 = this.minecraft;
            int var10001 = this.id;
            String var2 = this.name.trim();
            int var3 = var10001;
            Minecraft var4 = var10000;
            var10000.levelIo.saveOnline(var4.level, var4.host, var4.session.username, var4.session.sessionId, var2, var3);
            this.minecraft.setCurrentScreen((GuiScreen)null);
            this.minecraft.grabMouse();
         }

         if(button.id == BTN_CANCEL) {
            this.minecraft.setCurrentScreen(this.parent);
         }

      }
   }

   protected final void onKeyPress(char charTyped, int keyCodeTyped) {
      if(keyCodeTyped == 14 && this.name.length() > 0) {
         this.name = this.name.substring(0, this.name.length() - 1);
      }

      if("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789 ,.:-_\'*!\"#%/()=+?[]{}<>".indexOf(charTyped) >= 0 && this.name.length() < 64) {
         this.name = this.name + charTyped;
      }

      ((Button)this.buttons.get(BTN_SAVE)).active = this.name.trim().length() > 1;
   }

   public final void render(int var1, int var2) {
      drawFadingBox(0, 0, this.width, this.height, 1610941696, -1607454624);
      drawCenteredString(this.fontRenderer, this.title, this.width / 2, 40, 16777215);
      int var3 = this.width / 2 - 100;
      int var4 = this.height / 2 - 10;
      drawBox(var3 - 1, var4 - 1, var3 + 200 + 1, var4 + 20 + 1, -6250336);
      drawBox(var3, var4, var3 + 200, var4 + 20, -16777216);
      drawString(this.fontRenderer, this.name + (this.counter / 6 % 2 == 0?"_":""), var3 + 4, var4 + 6, 14737632);
      super.render(var1, var2);
   }
}
