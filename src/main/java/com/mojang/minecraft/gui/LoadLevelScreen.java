package com.mojang.minecraft.gui;

import com.mojang.minecraft.Minecraft;
import com.mojang.minecraft.level.Level;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;

public class LoadLevelScreen extends GuiScreen implements Runnable {

   protected final static int NUM_ONLINE_LEVELS = 5;
   protected final static int BTN_SAVE_LOAD = NUM_ONLINE_LEVELS;
   protected final static int BTN_CANCEL = NUM_ONLINE_LEVELS + 1;

   protected GuiScreen parent;
   private boolean finished = false;
   private boolean loaded = false;
   private String[] levels = null;
   private String status = "";
   protected String title = "Load level";
   boolean frozen = false;
   JFileChooser chooser;
   protected boolean saving = false;
   protected File selectedFile;


   public LoadLevelScreen(GuiScreen screen) {
      this.parent = screen;
   }

   public void run() {
      try {
         if(this.frozen) {
            try {
               Thread.sleep(100L);
            } catch (InterruptedException interrupted) {
               interrupted.printStackTrace();
            }
         }

         this.status = "Getting level list..";
         if (this.minecraft.host != null) {
            URL url = new URL("http://" + this.minecraft.host + "/listmaps.jsp?user=" + this.minecraft.session.username);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openConnection().getInputStream()));
            this.levels = reader.readLine().split(";");
            if (this.levels.length >= 5) {
               this.setLevels(this.levels);
               this.loaded = true;
               return;
            }

            this.status = this.levels[0];
         }
         this.finished = true;
      } catch (Exception exception) {
         exception.printStackTrace();
         this.status = "Failed to load levels";
         this.finished = true;
      }

   }

   protected void setLevels(String[] levels) {
      for(int i = 0; i < NUM_ONLINE_LEVELS; ++i) {
         ((Button)this.buttons.get(i)).active = !levels[i].equals("-");
         ((Button)this.buttons.get(i)).text = levels[i];
         ((Button)this.buttons.get(i)).visible = true;
      }

   }

   public void onOpen() {
      (new Thread(this)).start();

      for(int i = 0; i < NUM_ONLINE_LEVELS; ++i) {
         this.buttons.add(new Button(i, this.width / 2 - 100, this.height / 6 + i * 24, "---"));
         ((Button)this.buttons.get(i)).visible = false;
         ((Button)this.buttons.get(i)).active = false;
      }

      this.buttons.add(new Button(BTN_SAVE_LOAD, this.width / 2 - 100, this.height / 6 + 120 + 12, "Load file..."));
      this.buttons.add(new Button(BTN_CANCEL,    this.width / 2 - 100, this.height / 6 + 168, "Cancel"));
   }

   protected final void onButtonClick(Button button) {
      if(!this.frozen) {
         if(button.active) {
            if(this.loaded && button.id < NUM_ONLINE_LEVELS) {
               this.openLevel(button.id);
            }

            if((this.finished || this.loaded) && button.id == BTN_SAVE_LOAD) {
               this.frozen = true;
               LevelDialog dialog;
               (dialog = new LevelDialog(this)).setDaemon(true);
               SwingUtilities.invokeLater(dialog);
            }

            if((this.finished || this.loaded) && button.id == BTN_CANCEL) {
               this.minecraft.setCurrentScreen(this.parent);
            }
         }
      }
   }

   protected void openLevel(File saveFile) {
      File file = saveFile;
      Minecraft mc = this.minecraft;
      Level level;
      boolean var10000;
      if((level = this.minecraft.levelIo.load(file)) == null) {
         var10000 = false;
      } else {
         mc.setLevel(level);
         var10000 = true;
      }

      this.minecraft.setCurrentScreen(this.parent);
   }

   protected void openLevel(int onlineLevelNumber) {
      this.minecraft.loadOnlineLevel(this.minecraft.session.username, onlineLevelNumber);
      this.minecraft.setCurrentScreen((GuiScreen)null);
      this.minecraft.grabMouse();
   }

   public void render(int var1, int var2) {
      drawFadingBox(0, 0, this.width, this.height, 1610941696, -1607454624);
      drawCenteredString(this.fontRenderer, this.title, this.width / 2, 20, 16777215);
      if(this.frozen) {
         drawCenteredString(this.fontRenderer, "Selecting file..", this.width / 2, this.height / 2 - 4, 16777215);

         try {
            Thread.sleep(20L);
         } catch (InterruptedException var3) {
            var3.printStackTrace();
         }
      } else {
         if(!this.loaded) {
            drawCenteredString(this.fontRenderer, this.status, this.width / 2, this.height / 2 - 4, 16777215);
         }

         super.render(var1, var2);
      }
   }

   public final void onClose() {
      super.onClose();
      if(this.chooser != null) {
         this.chooser.cancelSelection();
      }

   }

   public final void tick() {
      super.tick();
      if(this.selectedFile != null) {
         this.openLevel(this.selectedFile);
         this.selectedFile = null;
      }

   }
}
