import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
public class startForm extends MIDlet implements CommandListener{
    static Search search=null;
    static Settings settings=null;
    static Downloads downloads=null;
    static StringItem sitem=new StringItem("Speed:", null);
    Display d;
    static Form frm;
    static startForm instance;
    public static MIDlet midlet;
    public static String searchString="Jai Shree Ram";
    public void startApp() {
        frm=new Form("YouDown 3");
        ImageItem imgItem = null;
        try {
            imgItem=new ImageItem(null, Image.createImage("/yt2.png"), Item.LAYOUT_CENTER, null);
        } catch (Exception ex) {}
        frm.append(imgItem);
        StringItem strItem=new StringItem(null, "\nYouDown 3\nFree youtube downloader for mobile devices.\nCreated By iAmMegamohan@Gmail.com");
        strItem.setLayout(Item.LAYOUT_CENTER);
        frm.append(strItem);
        frm.append(sitem);
        frm.addCommand(new Command("Search",Command.ITEM,0));
        frm.addCommand(new Command("Downloads",Command.ITEM,0));
        frm.addCommand(new Command("Settings",Command.ITEM,0));
        frm.addCommand(new Command("About",Command.ITEM,0));        
        frm.addCommand(new Command("Exit",Command.EXIT,0));
        frm.setCommandListener(this);
        Display.getDisplay(this).setCurrent(frm);
        
        // static initiliSation
        instance=this;
        midlet=this;
    }
    public void commandAction(Command c, Displayable d) {
        if(c.getLabel().equals("Downloads")) {
            if(downloads==null) 
                downloads=new Downloads();
            downloads.show();
        }
        if(c.getLabel().equals("Settings")) {
            if(settings==null) 
                settings=new Settings();
            settings.show();
        }
        if(c.getLabel().equals("Exit")) {
            notifyDestroyed();
            destroyApp(true);
        }
        if(c.getLabel().equals("About")) {
            Form about=new Form("About me");
            about.append("YouDown 3\n");
            about.append("Free youtube downloader for mobile devices...\n");
            about.append("Created by iAmMegamohan@gmail.com\n");
            about.append("Enjoy!!:)\n");
            about.append("If you want to get up-to-date news of my other new and cool apps then you can visit  my facebook page iAmMegamohan at www.facebook.com");
            about.append("\nNote:\n\nIf someone want to share some cool ideas for mobile apps then u can contact me on my email address::");
            about.setCommandListener(new CommandListener() {
                public void commandAction(Command c, Displayable d) {
                    show();
                }
            });
            about.addCommand(new Command("Back",Command.BACK,0));
            Display.getDisplay(midlet).setCurrent(about);
        }
        if(c.getLabel().equals("Search")) {
            final TextBox txt=new TextBox("Search youtube", searchString, 99, TextField.ANY);
            txt.setCommandListener(new CommandListener() {
                public void commandAction(Command c, Displayable d) {
                    if(c.getLabel().equals("Search")) {
                        searchString=txt.getString();
                        function.data.pageNo=1;
                        if(search==null)
                            search=new Search();
                        search.show(midlet);
                    }
                    if(c.getLabel().equals("Cancel")) {
                        instance.show();
                    }
                }
            });
            txt.addCommand(new Command("Search",Command.OK,0));
            txt.addCommand(new Command("Cancel",Command.CANCEL,0));
            Display.getDisplay(midlet).setCurrent(txt);
        }
    }
    public void pauseApp() {
    }    
    public void destroyApp(boolean unconditional) {
    }
    public static void show() {
        Display.getDisplay(midlet).setCurrent(frm);
    }
}
