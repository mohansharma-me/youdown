import javax.microedition.lcdui.*;
public class Settings implements CommandListener {
    Form frm;
    
    Gauge dwnlSpeed=new Gauge("Download block:\n", true, 300, storage.downloadSpeed);
    TextField txtFolderPath=new TextField("Folder path", storage.folderPath, 50, TextField.ANY);
    ChoiceGroup cg=new ChoiceGroup("Rename video", Choice.POPUP);
    public static class storage {
        public static int downloadSpeed=30;
        public static String folderPath="youdown3";
        public static boolean askFilename=true;
    }
    public Settings() {
        frm=new Form("Settings");
        frm.append(dwnlSpeed);
        frm.append(txtFolderPath);
        cg.append("Yes", null);
        cg.append("No", null);
        frm.append(cg);
        frm.addCommand(new Command("Back",Command.BACK,0));
        frm.addCommand(new Command("Save",Command.OK,0));
        frm.setCommandListener(this);
    }
    public void show() {        
        Display.getDisplay(startForm.midlet).setCurrent(frm);
    }
    public void commandAction(Command c, Displayable d) {
        if(c.getLabel().equals("Save")) {
            if(dwnlSpeed.getValue()<10) {
                storage.downloadSpeed=10;
            } else {
                storage.downloadSpeed=dwnlSpeed.getValue();
            }
            storage.folderPath=txtFolderPath.getString().trim();
            storage.folderPath=function.removeCharacters(storage.folderPath, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
            if(cg.getString(cg.getSelectedIndex()).equals("Yes")) {
                storage.askFilename=true;
            } else {
                storage.askFilename=false;
            }
            startForm.show();
            System.out.println("Speed:"+Settings.storage.downloadSpeed);
            Alert alert=new Alert("Information", "This settings is applied to this session of app!!", null, AlertType.INFO);
            alert.setTimeout(Alert.FOREVER);
            Display.getDisplay(startForm.midlet).setCurrent(alert);
        }
        
            startForm.show();
        
    }
}
