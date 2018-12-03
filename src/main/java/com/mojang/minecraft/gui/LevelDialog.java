package com.mojang.minecraft.gui;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.io.File;

final class LevelDialog extends Thread {

   // $FF: synthetic field
   private LoadLevelScreen screen;
   private static File suggestedFile  = getSuggestedFile();


   LevelDialog(LoadLevelScreen levelScreen) {
	   super();
      this.screen = levelScreen;
   }

   public final void run() {
      JFileChooser fileChooser = null;
      LoadLevelScreen scrn = null;
      try {
         LoadLevelScreen levelScreen = this.screen;
         fileChooser = new JFileChooser();
         levelScreen.chooser = fileChooser;
         FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("Minecraft levels", new String[]{"mine"});
         scrn = this.screen;
         this.screen.chooser.setFileFilter(fileFilter);
         this.screen.chooser.setMultiSelectionEnabled(false);
         int buttonPressed;
         if(this.screen.saving) {
             if (suggestedFile.getParentFile().exists()) {this.screen.chooser.setSelectedFile(suggestedFile);}
             buttonPressed = this.screen.chooser.showSaveDialog(this.screen.minecraft.canvas);
         } else {
             if (suggestedFile.exists()) { this.screen.chooser.setSelectedFile(suggestedFile);}
             buttonPressed = this.screen.chooser.showOpenDialog(this.screen.minecraft.canvas);
         }
         if(buttonPressed == 0) {
            this.screen.selectedFile = this.screen.chooser.getSelectedFile();
             suggestedFile = this.screen.selectedFile;
         }
      } finally {
         this.screen.frozen = false;
         fileChooser = null;
         this.screen.chooser = fileChooser;
      }

   }

   private static String getUserName() {
       String name = System.getProperty("user.name");
       name = name == null ? "guest" : name;
       return name;
   }

   private static File getSuggestedFile() {
       if (suggestedFile != null) {
           return suggestedFile;
       }
       String userHome = System.getProperty("user.home");
       String userDir  = System.getProperty("user.dir");
       String userName = getUserName();
       File homeDir = userHome == null ? null : new File(userHome);
       File dir = (userDir == null) ? null : new File(userDir);
       dir = homeDir == null || !homeDir.exists()? dir : homeDir;
       dir = dir != null && dir.exists() ? dir : FileSystemView.getFileSystemView().getRoots()[0];
       File file = new File(dir, userName);
       return file;
   }
}
