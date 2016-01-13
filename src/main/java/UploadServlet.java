// Import required java libraries
import java.io.*;
import java.util.*; 
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.output.*;

public class UploadServlet extends HttpServlet {
    private boolean isMultipart;
    private String filePathTemp;
    private int maxFileSize = 1024 * 1024;
    private int maxMemSize = 4 * 1024;
    private File file ;
    public void init( ){
        // Get the file location where it would be stored.
        filePathTemp =
                getServletContext().getInitParameter("file-upload");
    }
    public void doPost(HttpServletRequest request,
                       HttpServletResponse response)
            throws ServletException, IOException {
        // sum of path number
        int pathCount = 0;
        // file exits
        boolean fileExits = false;
        //check the enctype
        isMultipart = ServletFileUpload.isMultipartContent(request);
        response.setContentType("text/html");
        PrintWriter out = response.getWriter( );
        if( !isMultipart ){
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet upload</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<p>No file uploaded</p>");
            out.println("</body>");
            out.println("</html>");
            return;
        }

        DiskFileItemFactory factory = new DiskFileItemFactory();
        // maximum size that will be stored in memory
        factory.setSizeThreshold(maxMemSize);
        // Location to save data that is larger than maxMemSize.
        factory.setRepository(new File("c:\\temp"));
        // Create a new file upload handler
        ServletFileUpload upload = new ServletFileUpload(factory);
        // maximum file size to be uploaded.
        upload.setSizeMax( maxFileSize );

        try{
            // Parse the request to get file items.
            List fileItems = upload.parseRequest(request);
            // Process the uploaded file items
            Iterator i = fileItems.iterator();
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet upload</title>");
            out.println("</head>");
            out.println("<body>");
            List<String> filePath = new ArrayList<String>();
            while (i.hasNext () )
            {
                FileItem fi = (FileItem)i.next();
                //fi is a warehouse name
                if (fi.isFormField () && fi.getString().equals("on"))
                {
                    String str = filePathTemp + fi.getFieldName() + "\\";
                    filePath.add(str);
                    pathCount ++;
                }
                //fi is a module name
                else if(fi.isFormField () && fi.getFieldName().equals("module"))
                {
                    if(pathCount == 0)
                    {
                        throw new RuntimeException("you need choose at least one warehouse!");
                    }
                    for(int j = 0; j < pathCount; j++)
                    {
                        filePath.set(j,filePath.get(j)+fi.getString()+"\\");
                    }
                }
                // fi is a file
                else if(!fi.isFormField ())
                {
                    fileExits = true;
                    // Get the uploaded file parameters
                    String fieldName = fi.getFieldName();
                    String fileName = fi.getName();
                    String contentType = fi.getContentType();
                    boolean isInMemory = fi.isInMemory();
                    long sizeInBytes = fi.getSize();
                    // Write the file
//                    if( fileName.lastIndexOf("\\") >= 0 )
//                    {
                    for(int k = 0; k < pathCount; k++)
                    {
                        file = new File( filePath.get(k) +
                                fileName.substring( fileName.lastIndexOf("\\")) + 1) ;
                        fi.write( file ) ;
                    }

                    out.println("Uploaded Filename:" + fileName + "<br>");
                }
                if(!fileExits)
                {
                    throw new RuntimeException("you need to choose at least one file");
                }

            }
            out.println("</body>");
            out.println("</html>");
        }catch(Exception ex) {
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet upload</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<p>"+ex.getMessage()+"</p>");
            out.println("</body>");
            out.println("</html>");
        }
    }
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
            throws ServletException, IOException {

        throw new ServletException("GET method used with " +
                getClass( ).getName( )+":POST method required.");

    }

}