import javax.microedition.midlet.MIDlet;
import javax.microedition.lcdui.*;
public class Search implements CommandListener {
    threadSearch thread=null;
    static StringItem strerror=null; 
    public static Form frm;
    public static StringItem strProcess;
    public Search() {

    }
    public void show(MIDlet midlet) { 
        frm=new Form("Searching");
        frm.append("Search:\n"+startForm.searchString);
        strProcess=new StringItem("Processing...\n", "Fetching page#"+Integer.toString(function.data.pageNo));
        frm.append(strProcess);
        thread=new threadSearch();
        thread.DoStop=false;
        thread.start();
        frm.addCommand(new Command("Stop",Command.OK,0));
        frm.addCommand(new Command("Back",Command.BACK,0));
        frm.setCommandListener(this);
        Display.getDisplay(midlet).setCurrent(frm);
    }
    public void commandAction(Command c, Displayable d) {
        if(c.getLabel().equals("Back")) {
            startForm.show();
        }
        if(c.getLabel().equals("Stop")) {
            try {
                startForm.show();
                Thread newT=new Thread() {
                    public void run() {
                        try {
                            thread.DoStop=true;
                            thread.yield();
                        } catch (Exception ex) {
                            Alert alert=new Alert("StopT Error:", ex.getMessage()+"\n\n"+ex.toString(), null, AlertType.ERROR);
                            alert.setTimeout(Alert.FOREVER);
                            Display.getDisplay(startForm.midlet).setCurrent(alert);
                        }
                    }
                };
                newT.run();
            } catch (Exception ex) {
                Alert alert=new Alert("Stop Error:", ex.getMessage()+"\n\n"+ex.toString(), null, AlertType.ERROR);
                alert.setTimeout(Alert.FOREVER);
                Display.getDisplay(startForm.midlet).setCurrent(alert);
            }
        }
    }
    public static void showError(String msg) {
        if(strerror==null) {
            strerror=new StringItem("Error:\n", msg);
            frm.append(strerror);
        } else {
            strerror.setText(msg);
        }
    }
}
class threadSearch extends Thread {
    public static List lst=null;
    public static String pageData;
    public static boolean DoStop=false;
    public void run() {
            try {
                String temp=function.removeCharacters(startForm.searchString, "abcdefghijklmnopqrstuvwxyz ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_");
                temp=function.replaceAll(temp, " ", "%20");
                System.out.println("http://m.youtube.com/results?q="+temp.trim()+"&p="+Integer.toString(function.data.pageNo));
                function.data.searchPageData=function.dwnlPage("http://m.youtube.com/results?q="+temp.trim()+"&p="+Integer.toString(function.data.pageNo));
                if(function.data.searchPageData.length()<=0) {
                    Search.showError("Sorry, connection error! plz restart app!");
                } else {
                    pageData=function.data.searchPageData.replace((char)34, (char)39);
                    function.data.array=null;
                    String tmpStr=null;
                    int count=0,sp=-1,ep=-1;                            
                    while(true) { 
                        sp=pageData.indexOf("<a accesskey='",ep+1);
                        if(sp!=-1) {
                            ep=pageData.indexOf("</a>",sp);
                            if(ep!=-1) {
                                tmpStr=pageData.substring(sp+("<a accesskey=").length(),ep+4);
                                if(function.data.array==null)
                                    function.data.array=new List("Search page#"+Integer.toString(function.data.pageNo),List.IMPLICIT);
                                function.data.array.append(tmpStr, null);
                                count++;
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    }                        
                    Search.strProcess.setText("Found "+Integer.toString(count)+" videos.");
                    String item,tmp,id;
                    lst=function.data.array;
                    final List instList=lst;
                    for(int i=0;i<lst.size();i++) {
                        if(DoStop==false) {
                            item=lst.getString(i);
                            tmp=item.replace((char)34, (char)39);
                            id=function.stripTag(tmp,";v=","'>");
                            if(id.length()<=0) {
                                lst.delete(i);
                            } else {
                                tmp=function.stripTag(tmp, "'>", "</a>").trim();
                                tmp=function.decodeURL(tmp);
                                Image screenShot = null;
                                try {
                                    Search.strProcess.setText("Getting snapShot ["+Integer.toString(i)+"/"+Integer.toString(lst.size()) +"]");
                                    screenShot=function.getImage("http://i.ytimg.com/vi/"+id.trim()+"/default.jpg?w=120&amp;h=90&amp;");
                                } catch (Exception ex) {
                                    Alert alert=new Alert("SnapShot Error:", ex.getMessage()+"\n\n"+ex.toString(), null, AlertType.ERROR);
                                    alert.setTimeout(Alert.FOREVER);
                                    Display.getDisplay(startForm.midlet).setCurrent(alert);
                                }
                                tmp=function.removeCharacters(tmp, "abcdefghijklmnopqrstuvwxyz ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_()[]");
                                tmp=function.replaceAll(tmp, "quot", "");
                                tmp=function.replaceAll(tmp, "QUOT", "");
                                lst.set(i, tmp+"[VID]"+id.trim()+"[/VID]", screenShot);
                            }
                        }
                    }                 
                    lst.setFitPolicy(lst.TEXT_WRAP_ON);
                    lst.addCommand(new Command("Next",Command.ITEM,0));
                    lst.addCommand(new Command("Previous",Command.ITEM,0));
                    lst.addCommand(new Command("Back",Command.BACK,0));
                    lst.setCommandListener(new CommandListener() {
                        public void commandAction(Command c, Displayable d) {
                            if(c==instList.SELECT_COMMAND || c.getLabel().equals("Details")) {
                                String vid=instList.getString(instList.getSelectedIndex());
                                vid=function.stripTag(vid, "[VID]", "[/VID]").trim();
                                Video v=new Video(vid,instList.getImage(instList.getSelectedIndex()));
                                v.show();
                            }
                            if(c.getLabel().equals("Next")) {
                                function.data.pageNo++;
                                startForm.search.show(startForm.midlet);
                            }
                            if(c.getLabel().equals("Previous")) {
                                if(function.data.pageNo!=1) {
                                    function.data.pageNo--;
                                    startForm.search.show(startForm.midlet);                                    
                                }
                            }
                            if(c.getLabel().equals("Back")) {
                                startForm.show();
                            }
                        }
                    });
                    lst.setSelectCommand(new Command("Details",Command.OK,0));
                    if(DoStop==false) {
                        Display.getDisplay(startForm.midlet).setCurrent(lst);
                    }
                }
            } catch (Exception ex) {
                Alert alert=new Alert("Search Error:", ex.getMessage()+"\n\n"+ex.toString(), null, AlertType.ERROR);
                alert.setTimeout(Alert.FOREVER);
                Display.getDisplay(startForm.midlet).setCurrent(alert);            }
    }
}
