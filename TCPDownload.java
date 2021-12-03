
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPDownload implements Runnable
{
    int clientSocket;

    TCPDownload(int clientsocket) {        
    	this.clientSocket = clientsocket;  
    }

	@Override 
    public void run()
    {
            try
            {
                ObjectOutputStream objectOutput = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream objectInput = new ObjectInputStream(clientSocket.getInputStream());
        
                String fileName = (String)objectInput.readObject();
                File myFile = new File("./Files/"+fileName);

                String content = bufferedReader.readLine(myFile);

                if(content == null){
                   objectOutput.writeObject("DOWNLOAD-ERROR|"+(++RQ)+"|content does not exist");
                }

                List<String> chunks = new ArrayList<String>();
    
                while(chunks.size()*200 < content.length()){
                    if(chunks.size()*200+200 > content.length()){
                        chunks.add(new String(content.substring(chunks.size()*200,content.length())));
                    }else{
                        chunks.add(new String(content.substring(chunks.size()*200, chunks.size()*200+200)));
                    }
                }

                for(int i = 0; i< chunks.size(); i++){
                    Integer chunkNumber = i;
                    
                    //last chunk validation
                    if(i == chunks.size()-1){
                        objectOutput.writeObject("FILE-END|"+(++RQ)+"|"+fileNameDownload+"|"+chunkNumber+"|"+chunks); //TODO: need to modify for individual chunks
                    }else{
                        objectOutput.writeObject("FILE|"+(++RQ)+"|"+fileNameDownload+"|"+chunkNumber+"|"+chunks); //TODO: need to modify for individual chunks
                    }
                }
                
                myFile
                byte [] byte_arr = new byte[(int)length];
                
                objectOutput.writeObject((int)myFile.length());
                objectOutput.flush();
                
                FileInputStream fileInputStream =new FileInputStream(myFile);
                BufferedInputStream bufferInput = new BufferedInputStream(fileInputStream);
                bufferInput.read(byte_arr,0,(int)myFile.length());
                
                objectOutput.write(byte_arr,0,byte_arr.length);
                
                objectOutput.flush();                
            
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
    }
}

