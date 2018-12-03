package com.mojang.minecraft.gui;

import com.mojang.minecraft.Minecraft;
import java.io.File;

public final class SaveLevelScreen extends LoadLevelScreen {

   public SaveLevelScreen(GuiScreen screen) {
      super(screen);
      this.title = "Save level";
      this.saving = true;
   }

   public final void onOpen() {
      super.onOpen();
      ((Button)this.buttons.get(BTN_SAVE_LOAD)).text = "Save file...";
   }

   protected final void setLevels(String[] var1) {
      for(int i = 0; i < NUM_ONLINE_LEVELS; ++i) {
         ((Button)this.buttons.get(i)).text = var1[i];
         ((Button)this.buttons.get(i)).visible = true;
         ((Button)this.buttons.get(i)).active = this.minecraft.session.haspaid;
      }

   }

   public final void render(int var1, int var2) {
      super.render(var1, var2);
      if(!this.minecraft.session.haspaid) {
         drawFadingBox(this.width / 2 - 80, 72, this.width / 2 + 80, 120, -536870912, -536870912);
         drawCenteredString(this.fontRenderer, "Save to a local file, only!", this.width / 2, 80, 16748688);
         drawCenteredString(this.fontRenderer, "Purchase the game to be able", this.width / 2, 96, 14712960);
         drawCenteredString(this.fontRenderer, "to save your levels online.", this.width / 2, 104, 14712960);
      }
   }

   @Override
   protected final void openLevel(File saveFile) {
      if(!saveFile.getName().endsWith(".mine")) {
         saveFile = new File(saveFile.getParentFile(), saveFile.getName() + ".mine");
      }

      File file = saveFile;
      Minecraft mc = this.minecraft;
      this.minecraft.levelIo.save(mc.level, file);
      this.minecraft.setCurrentScreen(this.parent);
   }

   protected final void openLevel(int onlineLevelNumber) {
      this.minecraft.setCurrentScreen(new LevelNameScreen(this, ((Button)this.buttons.get(onlineLevelNumber)).text, onlineLevelNumber));
   }
}
