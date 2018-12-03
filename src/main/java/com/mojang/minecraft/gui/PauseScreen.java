package com.mojang.minecraft.gui;

public final class PauseScreen extends GuiScreen {

   private static final int BTN_OPTIONS  = 0;
   private static final int BTN_GENERATE = 1;
   private static final int BTN_SAVE     = 2;
   private static final int BTN_LOAD     = 3;
   private static final int BTN_BACK     = 4;

   public final void onOpen() {
      this.buttons.clear();
      this.buttons.add(new Button(BTN_OPTIONS,  this.width / 2 - 100, this.height / 4, "Options..."));
      this.buttons.add(new Button(BTN_GENERATE, this.width / 2 - 100, this.height / 4 + 24, "Generate new level..."));
      this.buttons.add(new Button(BTN_SAVE,     this.width / 2 - 100, this.height / 4 + 48, "Save level.."));
      this.buttons.add(new Button(BTN_LOAD,     this.width / 2 - 100, this.height / 4 + 72, "Load level.."));
      this.buttons.add(new Button(BTN_BACK,     this.width / 2 - 100, this.height / 4 + 120, "Back to game"));
      if(this.minecraft.session == null) {
         ((Button)this.buttons.get(BTN_SAVE)).active = false;
         ((Button)this.buttons.get(BTN_LOAD)).active = false;
      }

      if(this.minecraft.networkManager != null) {
         ((Button)this.buttons.get(BTN_GENERATE)).active = false;
         ((Button)this.buttons.get(BTN_SAVE    )).active = false;
         ((Button)this.buttons.get(BTN_LOAD    )).active = false;
      }

   }

   @Override
   protected final void onButtonClick(Button var1) {
      if(var1.id == BTN_OPTIONS) {
         this.minecraft.setCurrentScreen(new OptionsScreen(this, this.minecraft.settings));
      }

      if(var1.id == BTN_GENERATE) {
         this.minecraft.setCurrentScreen(new GenerateLevelScreen(this));
      }

      if(this.minecraft.session != null) {
         if(var1.id == BTN_SAVE) {
            this.minecraft.setCurrentScreen(new SaveLevelScreen(this));
         }

         if(var1.id == BTN_LOAD) {
            this.minecraft.setCurrentScreen(new LoadLevelScreen(this));
         }
      }

      if(var1.id == BTN_BACK) {
         this.minecraft.setCurrentScreen((GuiScreen)null);
         this.minecraft.grabMouse();
      }

   }

   @Override
   public final void render(int var1, int var2) {
      drawFadingBox(0, 0, this.width, this.height, 1610941696, -1607454624);
      drawCenteredString(this.fontRenderer, "Game menu", this.width / 2, 40, 16777215);
      super.render(var1, var2);
   }
}
