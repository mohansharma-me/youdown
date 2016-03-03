import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.ContentConnection;
import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.*;
public class function {
    static Displayable previous=null;
    static class data {
        public static List array=null;
        public static int pageNo=1;
        public static String searchPageData;
    }
    
    public static String removeCharacters(String text, String charsToKeep) {  
         StringBuffer buffer = new StringBuffer();  
         for(int i = 0; i < text.length(); i++) {  
             char ch = text.charAt(i);  
             if(charsToKeep.indexOf(ch) > -1) {  
                 buffer.append(ch);  
             }  
         }  
         return buffer.toString();  
     }  
    public static String replaceAll(String _text,String _searchStr,String _replacementStr) {
	StringBuffer sb=new StringBuffer();
	int searchStringPos=_text.indexOf(_searchStr);
	int startPos=0;
	int searchStringLength=_searchStr.length();
	while(searchStringPos!=-1) {
		sb.append(_text.substring(startPos,searchStringPos)).append(_replacementStr);
		startPos=searchStringPos+searchStringLength;
		searchStringPos=_text.indexOf(_searchStr,startPos);
	}
	sb.append(_text.substring(startPos,_text.length()));
	return sb.toString();
    }
    public static String stripTag(String data,String start,String end) {
        int sp,ep;
        String outcome=new String("");
        sp=data.indexOf(start);
        if(sp!=-1) {
            ep=data.indexOf(end, sp);
            if(ep!=-1) {
                outcome=data.substring(sp+start.length(), ep);                
            }
        }
        return outcome;
    }
    public static Image getImage(String url) throws Exception
    {
        ContentConnection connection = (ContentConnection) Connector.open(url);
        DataInputStream iStrm = connection.openDataInputStream();    

        ByteArrayOutputStream bStrm = null;    
        Image im = null;

        try
        {
          byte imageData[];
          int length = (int) connection.getLength();
          if (length != -1)
          {
            imageData = new byte[length];
            iStrm.readFully(imageData);
          }
          else  // Length not available...
          {       
            bStrm = new ByteArrayOutputStream();

            int ch;
            while ((ch = iStrm.read()) != -1)
              bStrm.write(ch);

            imageData = bStrm.toByteArray();
            bStrm.close();                
          }
          im = Image.createImage(imageData, 0, imageData.length);        
        }
        finally
        {
          if (iStrm != null)
            iStrm.close();
          if (connection != null)
            connection.close();
          if (bStrm != null)
            bStrm.close();                              
        }
        return (im == null ? null : im);
    } 
    public static String dwnlPage(String url) throws Exception
    {
        HttpConnection connection = (HttpConnection) Connector.open(url);    
        connection.setRequestMethod(connection.getRequestMethod());
        connection.setRequestProperty("Content-Type","text/html"); // not neccessary
        connection.setRequestProperty("Content-Language", "en-US");  // not neccessary
        connection.setRequestProperty("Accept", "*/*");  // not neccessary
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.2.3) Gecko/20100401");
        DataInputStream iStrm = connection.openDataInputStream();
        ByteArrayOutputStream bStrm = null;    
        String outputString=new String();
        try
        {
          byte imageData[];
          int length = (int) connection.getLength();
          if (length != -1)
          {
            imageData = new byte[length];
            iStrm.readFully(imageData);
          }
          else  // Length not available...
          {       
            bStrm = new ByteArrayOutputStream();

            int ch;
            while ((ch = iStrm.read()) != -1)
              bStrm.write(ch);

            imageData = bStrm.toByteArray();
            outputString=new String(bStrm.toString());
            bStrm.close();                
          }        
        }
        finally
        {
          if (iStrm != null)
            iStrm.close();
          if (connection != null)
            connection.close();
          if (bStrm != null)
            bStrm.close();                              
        }
        return outputString;
    }
    public static byte[] dwnlVideo(String url) throws Exception
    {
        HttpConnection connection = (HttpConnection) Connector.open(url);    
        connection.setRequestMethod(connection.getRequestMethod());
        connection.setRequestProperty("Content-Type","text/html"); // not neccessary
        connection.setRequestProperty("Content-Language", "en-US");  // not neccessary
        connection.setRequestProperty("Accept", "*/*");  // not neccessary
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.2.3) Gecko/20100401");
        DataInputStream iStrm = connection.openDataInputStream();
        ByteArrayOutputStream bStrm = null;    
        byte outByte[] = null;
        try
        {
          byte imageData[];
          int length = (int) connection.getLength();
          if (length != -1)
          {
            imageData = new byte[length];
            iStrm.readFully(imageData);
          }
          else  // Length not available...
          {       
            bStrm = new ByteArrayOutputStream();

            int ch;
            while ((ch = iStrm.read()) != -1)
              bStrm.write(ch);

            imageData = bStrm.toByteArray();
            outByte=imageData;
            bStrm.close();                
          }        
        }
        finally
        {
          if (iStrm != null)
            iStrm.close();
          if (connection != null)
            connection.close();
          if (bStrm != null)
            bStrm.close();                              
        }
        return outByte;
    }
    public static String[] Split(String splitStr, String delimiter) {  
     StringBuffer token = new StringBuffer();  
     Vector tokens = new Vector();  
     // split  
     char[] chars = splitStr.toCharArray();  
     for (int i=0; i < chars.length; i++) {  
         if (delimiter.indexOf(chars[i]) != -1) {  
            // we bumbed into a delimiter  
             if (token.length() > 0) {  
                 tokens.addElement(token.toString());  
                 token.setLength(0);  
             }  
         } else {  
             token.append(chars[i]);  
        }  
     }  
     // don't forget the "tail"...  
     if (token.length() > 0) {  
         tokens.addElement(token.toString());  
     }  
     // convert the vector into an array  
     String[] splitArray = new String[tokens.size()];  
     for (int i=0; i < splitArray.length; i++) {  
         splitArray[i] = tokens.elementAt(i).toString();  
     }  
     return splitArray;  
    }  
    public static String decodeURL(String s) {
  
    ByteArrayOutputStream out = new ByteArrayOutputStream(s.length());
  
    for (int i = 0; i < s.length(); i++) {
      int c = (int) s.charAt(i);
      if (c == '+') {
        out.write(' ');
      }
      else if (c == '%') {
        int c1 = Character.digit(s.charAt(++i), 16);
        int c2 = Character.digit(s.charAt(++i), 16);
        out.write((char) (c1 * 16 + c2));
      }
      else {
        out.write(c);
      }
    } // end for

    return out.toString();
   
    }
    public static String[] divString(String original,String separator) {
        Vector nodes = new Vector();
        // Parse nodes into vector
        int index = original.indexOf(separator);
        while(index >= 0) {
            nodes.addElement( original.substring(0, index) );
            original = original.substring(index+separator.length());
            index = original.indexOf(separator);
        }
        // Get the last node
        nodes.addElement( original );

         // Create split string array
        String[] result = new String[ nodes.size() ];
        if( nodes.size() > 0 ) {
            for(int loop = 0; loop < nodes.size(); loop++)
            {
                result[loop] = (String)nodes.elementAt(loop);
            }

        }
       return result;
    }

}