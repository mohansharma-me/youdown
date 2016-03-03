import java.io.IOException;
import javax.microedition.media.MediaException;
import javax.microedition.midlet.MIDlet;
import javax.microedition.lcdui.*;
import javax.microedition.media.Manager;
import javax.microedition.media.Player;
import javax.microedition.media.control.VideoControl;
public class Video implements CommandListener{
    static List tmpList,tmpList1;
    public static String pcPageData;
    public static StringItem strProcess=new StringItem(null,null);
    Form frm;
    Player p;
    int indexOfPlayer;
    boolean videoSet=false;
    static class video {
        public static boolean isPlay=false;
        public static String videoUID,videoTitle,videoTime,videoDesc,videoLikes,videoRTSP,videoDislikes,videoKeywords;
    }
    public Video(String vid,Image imgScreenshot) {
        video.videoUID=new String(vid);
        frm=new Form("Getting video details..");
        frm.setTicker(new Ticker("Please wait until the video details is fetched!"));
        ImageItem imgItem=new ImageItem(null, imgScreenshot, Item.LAYOUT_CENTER, "Screenshot of selected video.");
        indexOfPlayer=frm.append(imgItem);
        strProcess=new StringItem("Processing....\n", null);
        strProcess.setLayout(Item.LAYOUT_LEFT);
        frm.append(strProcess);
        frm.addCommand(new Command("Back",Command.BACK,0));
        frm.setCommandListener(this);
    }
    public void show() {
        strProcess.setText("Connecting to RTSP server...");
        Thread getRTSP=new Thread() {
            public void run() {
                try {
                    String vidPage=function.dwnlPage("http://m.youtube.com/watch?v="+video.videoUID.trim());
                    video.videoRTSP=("rtsp://").concat(function.stripTag(vidPage.replace((char)34, (char)39),"<a href='rtsp://","video.3gp")).concat("video.3gp").trim();
                    strProcess.setText("Getting video meta-tags...");
                    pcPageData=function.dwnlPage("http://www.youtube.com/watch?v="+video.videoUID.trim());
                    video.videoDesc=function.stripTag(pcPageData.replace((char)34, (char)39), "<meta name='description' content='", "'>");
                    video.videoKeywords=function.stripTag(pcPageData.replace((char)34, (char)39), "<meta name='keywords' content='", "'>");
                    video.videoTitle=function.stripTag(pcPageData.replace((char)34, (char)39), "<meta name='title' content='", "'>");
                    String temp=function.stripTag(threadSearch.pageData, video.videoTitle, "</td>");
                    video.videoTime=function.stripTag(temp,"<div style='color:#333;font-size:80%'>","&nbsp;");
                    video.videoLikes=function.stripTag(temp, video.videoTime, "'>");
                    video.videoLikes=function.stripTag(temp, video.videoLikes+"'>", "</span>");
                    video.videoDislikes=function.stripTag(temp, "likes,", "'>");
                    video.videoDislikes=function.stripTag(temp, video.videoDislikes+"'>", "</span>");
                    frm.setTicker(new Ticker("Video details is fetched! To download this video plz select Download option from Option menu."));
                    frm.setTitle(video.videoTitle);
                    strProcess.setLabel("\n");
                    strProcess.setText("Description:\n"+video.videoDesc+"\n\nKeywords:\n"+video.videoKeywords+"\n\nDuration:\n"+video.videoTime+"\n\nLikes:\n"+video.videoLikes+"\n\nDislikes:\n"+video.videoDislikes+"\n\nVideo ID:\n"+video.videoUID);
                    frm.addCommand(new Command("Play/Stop",Command.ITEM,0));
                    frm.addCommand(new Command("Download",Command.ITEM,0));
                } catch (Exception ex) {
                    Alert alert=new Alert("Error:", ex.getMessage()+"\n\n"+ex.toString(), null, AlertType.ERROR);
                    alert.setTimeout(Alert.FOREVER);
                    Display.getDisplay(startForm.midlet).setCurrent(alert);
                }
            }
        };
        getRTSP.start();
        Display.getDisplay(startForm.midlet).setCurrent(frm);
    }
    public void commandAction(Command c, Displayable d) {
        if(c.getLabel().equals("Download")) {
            pcPageData=pcPageData.replace((char)34, (char)39);
            String allData=function.stripTag(pcPageData, "url_encoded_fmt_stream_map=", "endscreen");
            for(int pp=0;pp<10;pp++)
                allData=function.decodeURL(allData);
            tmpList=new List("list", List.IMPLICIT);
            String tmpStr=null;
            int count=0,sp=-1,ep=-1;                            
            while(true) {
                sp=allData.indexOf("http://",ep+1);
                if(sp!=-1) {
                    ep=allData.indexOf(";",sp);
                    if(ep!=-1) {
                        tmpStr=allData.substring(sp+("http://").length(),ep+1);
                        tmpList.append(tmpStr, null);
                        count++;
                    } else {
                        break;
                    }
                } else {
                    break;
                }
            }
            tmpList1=new List("final", List.IMPLICIT);
            for(int pp=0;pp<tmpList.size();pp++) {
                tmpStr=tmpList.getString(pp);
                int tmpInt1=-1;
                int tmpInt2=-1;
                tmpInt1=tmpStr.indexOf("url=");
                tmpInt2=tmpStr.indexOf("itag=");
                if(tmpInt2!=-1) {
                    if(tmpInt1!=-1) {
                        String tmpArr[]=function.divString(tmpStr, ",url=");
                        tmpList1.append(function.replaceAll(("http://").concat(tmpArr[0]),";",""),null);
                        tmpList1.append(function.replaceAll(("").concat(tmpArr[1]),";",""),null);
                    } else {
                        tmpList1.append(function.replaceAll(("http://").concat(tmpStr),";",""),null);
                    }
                }
            }
            for(int ip=0;ip<tmpList1.size();ip++) {
                int startPos=-1,endPos=-1;
                startPos=tmpList1.getString(ip).indexOf("type=video/");
                endPos=tmpList1.getString(ip).indexOf("&", startPos);
                if(endPos!=-1) {
                    String yyy=tmpList1.getString(ip).substring(0, endPos);
                    tmpList1.set(ip, yyy, null);
                } else {
                    String yyy=tmpList1.getString(ip);
                    tmpList1.set(ip, yyy, null);
                }
            }
            tmpList=new List("Select video format", List.IMPLICIT);
            String ftype=".ext";
            for(int ab=0;ab<tmpList1.size();ab++) {
                String strItag=function.stripTag(tmpList1.getString(ab).trim(), "itag=", "&");
                Image img = null;
                try {
                    img=Image.createImage("/download.png");
                } catch (Exception ex) {}
                if(strItag.equals("5")) {
                    tmpList.append("FLV (240p)\nLow quality.",img);
                    ftype="-240p.flv";
                }
                if(strItag.equals("34")) {
                    tmpList.append("FLV (360p)\nMedium quality.",img);
                    ftype="-360p.flv";
                }
                if(strItag.equals("35")) {
                    tmpList.append("FLV (480p)\nHigh quality.",img);
                    ftype="-480p.flv";
                }
                if(strItag.equals("18")) {
                    tmpList.append("MP4 (360p)\nLow quality.",img);
                    ftype="-360p.mp4";
                }
                if(strItag.equals("22")) {
                    tmpList.append("MP4 (720p)\nMedium quality.",img);
                    ftype="-720p.mp4";
                }
                if(strItag.equals("37")) {
                    tmpList.append("MP4 (1080p)\nHigh quality.",img);
                    ftype="-1080.mp4";
                }
                if(strItag.equals("38")) {
                    tmpList.append("MP4 (HD)\nHigh defination.",img);
                    ftype="-HD.mp4";
                }
                if(strItag.equals("43")) {
                    tmpList.append("WEBM (360p)\nLow quality.",img);
                    ftype="-360p.webm";
                }
                if(strItag.equals("44")) {
                    tmpList.append("WEBM (480p)\nMedium quality.",img);
                    ftype="-480p.webm";
                }
                if(strItag.equals("45")) {
                    tmpList.append("WEBM (720p)\nHigh quality.",img);
                    ftype="-720p.webm";
                }
                if(strItag.equals("17")) {
                    tmpList.append("3GP Video\nBest quality.",img);
                    ftype=".3gp";
                }
            }
            
            final List tList=tmpList;
            final List tList1=tmpList1;
            tmpList.setCommandListener(new CommandListener() {
                public void commandAction(Command c, Displayable d) {
                    if(c.getLabel().equals("Save")) {
                        String link=tList1.getString(tList.getSelectedIndex()).trim();
                        String strItag=function.stripTag(link, "itag=", "&");
                        String ftype=".ext";
                        if(strItag.equals("5")) {
                            ftype="-240p.flv";
                        }
                        if(strItag.equals("34")) {
                            ftype="-360p.flv";
                        }
                        if(strItag.equals("35")) {
                            ftype="-480p.flv";
                        }
                        if(strItag.equals("18")) {
                            ftype="-360p.mp4";
                        }
                        if(strItag.equals("22")) {
                            ftype="-720p.mp4";
                        }
                        if(strItag.equals("37")) {
                            ftype="-1080.mp4";
                        }
                        if(strItag.equals("38")) {
                            ftype="-HD.mp4";
                        }
                        if(strItag.equals("43")) {
                            ftype="-360p.webm";
                        }
                        if(strItag.equals("44")) {
                            ftype="-480p.webm";
                        }
                        if(strItag.equals("45")) {
                            ftype="-720p.webm";
                        }
                        if(strItag.equals("17")) {
                            ftype=".3gp";
                        }
                        
                        if(startForm.downloads==null)
                            startForm.downloads=new Downloads();
                        final download dwnl=startForm.downloads.addDownload(link, video.videoTitle, ftype);
                        if(Settings.storage.askFilename) {
                            final TextBox tb=new TextBox("Enter filename:", video.videoTitle, 100, 0);
                            tb.addCommand(new Command("Submit",Command.OK,0));
                            tb.addCommand(new Command("Cancel",Command.CANCEL,0));
                            tb.setCommandListener(new CommandListener() {
                                public void commandAction(Command c, Displayable d) {
                                    if(c.getLabel().equals("Submit")) {
                                        dwnl.filename=tb.getString().trim();
                                    } 
                                    function.previous=tmpList;startForm.downloads.show();
                                    dwnl.start();
                                }
                            });
                            Display.getDisplay(startForm.midlet).setCurrent(tb);
                        } else {
                            function.previous=tmpList;startForm.downloads.show();
                            dwnl.start();
                        }
                    }
                    if(c.getLabel().equals("Back")) {
                        Display.getDisplay(startForm.midlet).setCurrent(frm);
                    }
                }
            });
            tmpList.setSelectCommand(new Command("Save",Command.OK,0));
            tmpList.addCommand(new Command("Back",Command.BACK,0));
            Display.getDisplay(startForm.midlet).setCurrent(tmpList);
        }
        if(c.getLabel().equals("Play/Stop")) {
            if(video.isPlay==false) {
                if(videoSet==false) {
                    // create new video player
                    try {
                        p = Manager.createPlayer(video.videoRTSP.trim());
                        p.setLoopCount(1);
                        p.realize();
                        VideoControl video = (VideoControl) p.getControl("VideoControl");
                        Item videoItem = (Item)video.initDisplayMode(VideoControl.USE_GUI_PRIMITIVE, null);
                        videoItem.setPreferredSize(190, 150);
                        videoItem.setLayout(Item.LAYOUT_CENTER);
                        frm.delete(indexOfPlayer);
                        frm.insert(indexOfPlayer, videoItem);
                        Video.video.isPlay=videoSet=true;
                        p.start();                        
                    } catch (Exception ex) {
                        Alert alert=new Alert("Play Error:", ex.getMessage()+"\n\n"+ex.toString(), null, AlertType.ERROR);
                        alert.setTimeout(Alert.FOREVER);
                        Display.getDisplay(startForm.midlet).setCurrent(alert);
                    }
                } else {
                    try {
                        p.start();
                        Video.video.isPlay=true;
                    } catch (Exception ex) {
                        Alert alert=new Alert("Resume Error:", ex.getMessage()+"\n\n"+ex.toString(), null, AlertType.ERROR);
                        alert.setTimeout(Alert.FOREVER);
                        Display.getDisplay(startForm.midlet).setCurrent(alert);
                    }
                }
            } else {
                try {
                    p.stop();
                    Video.video.isPlay=false;
                } catch (Exception ex) {
                    Alert alert=new Alert("Stop Error:", ex.getMessage()+"\n\n"+ex.toString(), null, AlertType.ERROR);
                    alert.setTimeout(Alert.FOREVER);
                    Display.getDisplay(startForm.midlet).setCurrent(alert);
                }
            }
        }
        if(c.getLabel().equals("Back")) {
            if(videoSet==true) {
                try {
                    p.stop();
                } catch (Exception ex) {}
                p.deallocate();
            }
            Video.video.isPlay=false;
            videoSet=false;
            Display.getDisplay(startForm.midlet).setCurrent(threadSearch.lst);
        }
    }
}