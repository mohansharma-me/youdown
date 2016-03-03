import com.sun.midp.io.ConnectionBaseAdapter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.ContentConnection;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.file.FileConnection;
import javax.microedition.lcdui.*;
public class Downloads implements CommandListener {
    static int curIndex=-1;
    static Form frm;
    // <--  downloader components
        static StringItem strStatus=new StringItem("Status:\n", null);    
        static Gauge pb=new Gauge(null, false, 100, 0);
        static StringItem strInfo=new StringItem("Downloaded:\n", null);
        static StringItem strVideo=new StringItem("Video details:", null);    
        static List list;
    // -->
    download[] dwnl=new download[0];
    public Downloads() {
        list=new List("Downloads", List.IMPLICIT);
        list.setSelectCommand(new Command("Select",Command.OK,0));
        list.addCommand(new Command("Back",Command.BACK,0));
        list.setFitPolicy(list.TEXT_WRAP_ON);
        list.setCommandListener(this);
        
        frm=new Form("%title%");
        strStatus.setLayout(Item.LAYOUT_LEFT);
        frm.append(strStatus);
        frm.append(pb);
        strInfo.setLayout(Item.LAYOUT_CENTER);
        frm.append(strInfo);
        strVideo.setLayout(Item.LAYOUT_LEFT);
        frm.append(strVideo);
        frm.addCommand(new Command("Redownload/Retry", Command.ITEM, 0));
        frm.addCommand(new Command("Pause/Resume", Command.ITEM, 0));
        frm.addCommand(new Command("Copy Link", Command.ITEM, 0));
        frm.addCommand(new Command("Stop Now", Command.ITEM, 0)); 
        frm.addCommand(new Command("Back", Command.BACK, 0));
        frm.setCommandListener(this);
    }
    public void show() {
        Display.getDisplay(startForm.midlet).setCurrent(list);
    }
    public download addDownload(String url,String video,String vtype) {
        int len=dwnl.length;
        int ind=list.append(function.removeCharacters(video, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890_- ()")+"<"+vtype.substring(vtype.lastIndexOf('.')+1, vtype.length()).toUpperCase() +">", null);
        download[] tmp=new download[len+1];
        tmp[len]=new download(video, url, vtype, ind);
        System.arraycopy(dwnl, 0, tmp, 0, len);
        dwnl=tmp;
        return dwnl[dwnl.length-1];
    }
    public void commandAction(Command c, Displayable d) {
        if(d.getClass().getName().equals("javax.microedition.lcdui.List")) {
            if(c.getLabel().equals("Select")) {
                curIndex=list.getSelectedIndex();
                Downloads.frm.setTitle(dwnl[curIndex].videoTitle);
                Downloads.pb.setMaxValue(100);
                Downloads.pb.setValue(0);
                Downloads.strStatus.setText(dwnl[curIndex].status);
                Downloads.strInfo.setText("0/0");
                Downloads.strVideo.setText("\nTitle:\n"+dwnl[curIndex].videoTitle+"\nType:\n"+dwnl[curIndex].videoType+"\nFilename:\n"+dwnl[curIndex].filename);
                Display.getDisplay(startForm.midlet).setCurrent(frm);
            }
            if(c.getLabel().equals("Back")) {
                if(function.previous!=null) {
                    Display.getDisplay(startForm.midlet).setCurrent(function.previous);
                    function.previous=null;
                } else {
                    startForm.show();
                }
            }            
        } else {
            if(c.getLabel().equals("Redownload/Retry")) {
                strStatus.setText("Checking...");
                if(dwnl[curIndex].started)
                    dwnl[curIndex].stopNow=true;
                String ttt=dwnl[curIndex].filename;
                dwnl[curIndex]=new download(dwnl[curIndex].videoTitle, dwnl[curIndex].videoLink,dwnl[curIndex].videoType, curIndex);
                dwnl[curIndex].start();
            }
            if(c.getLabel().equals("Pause/Resume")) {
                if(dwnl[curIndex].pauseNow) {
                    dwnl[curIndex].pauseNow=false;
                } else {
                    dwnl[curIndex].pauseNow=true;
                }
            }
            if(c.getLabel().equals("Stop Now")) {
                try {
                     dwnl[curIndex].stopNow=true;
                } catch(Exception ex) {}
            }
            if(c.getLabel().equals("Copy Link")) {
                TextBox tb=new TextBox("Video link:", dwnl[curIndex].videoLink, dwnl[curIndex].videoLink.length()*2, 0);
                tb.setCommandListener(new CommandListener() {
                    public void commandAction(Command c, Displayable d) {
                        if(c.getLabel().equals("Back")) {
                            show();
                        }
                    }
                });
                tb.addCommand(new Command("Back",Command.BACK,0));
                Display.getDisplay(startForm.midlet).setCurrent(tb);
            }
            if(c.getLabel().equals("Back")) {
                curIndex=-1;
                show();
            }
        }
    }
    
}
class download extends Thread {
    boolean started=false;
    boolean pauseNow=false;
    boolean stopNow=false;
    int listIndex;
    TextBox tb=null;
    String videoTitle,videoLink,videoType;
    String filename=null,temp,filepath,status;
    public download(String vTitle,String vLink,String vType,int index) {
        this.videoLink=vLink;
        this.videoTitle=vTitle;
        this.videoType=vType;
        this.listIndex=index;
    } 
    public void run() {
        started=true;
        if(filename==null) {
            temp=function.removeCharacters(videoTitle, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890_- ()");
        } else {
            temp=function.removeCharacters(filename, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890_- ()");
        }
        if(temp.length()>=30) {
            temp="youdown3_".concat(temp.substring(0, 29)).concat(videoType);            
        } else {
            temp="youdown3_".concat(temp).concat(videoType);            
        }
        filename=temp;
        if(Settings.storage.folderPath.length()>0) {
            Settings.storage.folderPath=function.replaceAll(Settings.storage.folderPath.trim(), "/", "");
            filepath="file:///e:/"+Settings.storage.folderPath.trim();
            try {
                FileConnection fconn=(FileConnection)Connector.open(filepath,Connector.READ_WRITE);
                if(!fconn.exists()) {
                    fconn.mkdir();
                } else {
                    if(!fconn.isDirectory()) {
                        fconn.delete();
                        fconn.mkdir();
                    }
                }
                fconn.close();
            } catch (Exception ex) {
                filepath="file:///e:";
            }
            filepath=filepath+"/"+filename;
        } else {
            filepath="file:///e:/"+filename;
        } 
        //filepath="file:///root1/"+filename;
        boolean flag=false;
        FileConnection conn = null;
        OutputStream os=null;
        DataOutputStream out=null;
        ContentConnection connection=null;
        DataInputStream iStrm=null;
        try {
            conn=(FileConnection)Connector.open(filepath.trim(),Connector.READ_WRITE);
            boolean ddd=conn.exists();
            int temp_count=0;
            while(ddd) {
                if(temp_count<9) {
                    conn=(FileConnection)Connector.open(function.replaceAll(filepath.trim(), ".", "("+temp_count+")."),Connector.READ_WRITE);
                    temp_count++;
                    ddd=conn.exists();
                } else {
                    break;
                }
            }
            if(conn.exists())
                conn.delete();
            conn.create();
            os=conn.openOutputStream();
            out=new DataOutputStream(os);
            connection = (ContentConnection) Connector.open(videoLink.trim(),Connector.READ);
            iStrm = connection.openDataInputStream();  
            
            flag=true;
        } catch(Exception ex) {
            flag=false;
            ex.printStackTrace();
        }
        int length = (int) connection.getLength();
        if(length<0) {
            //flag=false;
        }
        if(flag) {
            int sum=0,size=1024*Settings.storage.downloadSpeed;
            byte[] buffer=new byte[size];
            boolean done=false;
            if(length==0) {
                while(!done) {
                    if(stopNow) {
                        if(Downloads.curIndex==listIndex) {
                            Downloads.strStatus.setText("Stoped!");
                            setOuterStatus("STOPED");
                            status="Stoped!";
                        }
                        break;
                    }
                    while(pauseNow) {
                        try {
                            if(Downloads.curIndex==listIndex) {
                                Downloads.strStatus.setText("Paused!");
                                setOuterStatus("PAUSED");
                                status="Paused!";
                            }
                            Thread.sleep(10000);
                        } catch (Exception ex) {}
                    }
                    try {
                        int byteread=iStrm.read();
                        if(byteread==-1) {
                            if(Downloads.curIndex==listIndex) {
                                if(sum==0) {
                                    Downloads.strStatus.setText("Download terminated!");
                                    setOuterStatus("TERM.");
                                    status="Download terminated!";
                                } else {
                                    setOuterStatus("DONE");
                                    Downloads.strStatus.setText("Completed!");
                                    status="Completed!";
                                }
                                
                            } else {
                                setOuterStatus("DONE");
                                Alert alert=new Alert("Done:"+filename,videoTitle+"\n\rDownload complete!" , null, AlertType.INFO);
                                Display.getDisplay(startForm.midlet).setCurrent(alert);
                            }
                            done=true;
                        } else {
                            sum++;
                            startForm.sitem.setText("unCounted Kb.");
                            out.write(byteread);
                            if(!Downloads.list.getString(listIndex).substring(0, 5).equals("[...]")) {
                                setOuterStatus("...");
                            }
                            //process
                            if(Downloads.curIndex==listIndex) {
                                if(!Downloads.frm.getTitle().equals(videoTitle)) {
                                    Downloads.frm.setTitle(videoTitle);
                                }
//                                Downloads.pb.setMaxValue(length);
//                                Downloads.pb.setValue(sum);
                                Downloads.strStatus.setText("Downloading...");
                                status="Downloading...";
                                Downloads.strInfo.setText((sum/1024)+"/-- Kb.");
                                Downloads.strVideo.setText("\nTitle:\n"+videoTitle+"\nType:\n"+videoType+"\nFilename:\n"+filename);
                            }
                        }
                    } catch(Exception ex) {
                        Alert alert=new Alert("Error:"+filename,ex.getMessage()+"\n\r"+ex.toString() , null, AlertType.ERROR);
                        Display.getDisplay(startForm.midlet).setCurrent(alert);
                    }
                }
            } else {
                while(!done) {
                    if(stopNow) {
                        if(Downloads.curIndex==listIndex) {
                            Downloads.strStatus.setText("Stoped!");
                            setOuterStatus("STOPED");
                            status="Stoped!";
                        }
                        break;
                    }
                    while(pauseNow) {
                        try {
                            if(Downloads.curIndex==listIndex) {
                                Downloads.strStatus.setText("Paused!");
                                setOuterStatus("PAUSED");
                                status="Paused!";
                            }
                            Thread.sleep(10000);
                        } catch (Exception ex) {}
                    }
                    try {
                        int bytesread=iStrm.read(buffer); 
                        if(bytesread==-1) {
                            //complete 
                            if(Downloads.curIndex==listIndex) {
                                if(sum==0) {
                                    Downloads.strStatus.setText("Download terminated!");
                                    setOuterStatus("TERM.");
                                    status="Download terminated!";
                                } else {
                                    setOuterStatus("DONE");
                                    Downloads.strStatus.setText("Completed!");
                                    status="Completed!";
                                }
                            } else {
                                setOuterStatus("DONE");
                                Alert alert=new Alert("Done:"+filename,videoTitle+"\n\rDownload complete!" , null, AlertType.INFO);
                                Display.getDisplay(startForm.midlet).setCurrent(alert);
                            }
                            done=true;
                        } else {
                            startForm.sitem.setText((bytesread/1024)+" Kb.");
                            out.write(buffer, 0, bytesread);
                            sum=sum+bytesread;
                            if(!Downloads.list.getString(listIndex).substring(0, 5).equals("[...]")) {
                                setOuterStatus("...");
                            }
                            //process
                            if(Downloads.curIndex==listIndex) {
                                if(!Downloads.frm.getTitle().equals(videoTitle)) {
                                    Downloads.frm.setTitle(videoTitle);
                                }
                                Downloads.pb.setMaxValue(length);
                                Downloads.pb.setValue(sum);
                                Downloads.strStatus.setText("Downloading...");
                                status="Downloading...";
                                Downloads.strInfo.setText((sum/1024)+"/"+(length/1024)+" Kb.");
                                Downloads.strVideo.setText("\nTitle:\n"+videoTitle+"\nType:\n"+videoType+"\nFilename:\n"+filename);
                            }
                        }
                    } catch(Exception ex) {
                        Alert alert=new Alert("Error:"+filename,ex.getMessage()+"\n\r"+ex.toString() , null, AlertType.ERROR);
                        Display.getDisplay(startForm.midlet).setCurrent(alert);
                    }
                }
            }
        } else {
            Downloads.strStatus.setText("Download terminated!");
            setOuterStatus("TERM.");
            status="Download terminated!";
            System.out.println("download terminated!\n"+videoLink);
        }
        /*if(stopNow) {
            Alert alert=new Alert("Stoped:"+filename,videoTitle+"\n\rStoped!" , null, AlertType.WARNING);
            Display.getDisplay(startForm.midlet).setCurrent(alert);
        }*/
        if(conn!=null) {
            try { conn.close(); } catch (Exception ex) {}
        }
        if(os!=null) {
            try { os.close(); } catch (Exception ex) {}
        }
        if(out!=null) {
            try { out.close(); } catch (Exception ex) {}
        }
        if(connection!=null) {
            try { connection.close(); } catch (Exception ex) {}
        }
        if(iStrm!=null) {
            try { iStrm.close(); } catch (Exception ex) {}
        }
        started=false;
    }
    public void setOuterStatus(String msg) {
        String temp=Downloads.list.getString(listIndex);
        if(temp.indexOf("]")>0) {
            String tmp=function.stripTag(temp, "[", "]");
            temp=function.replaceAll(temp, tmp, msg);
            Downloads.list.set(listIndex, temp, null);
        } else {
            Downloads.list.set(listIndex, "["+msg+"]"+temp, null);
        }
    }
}