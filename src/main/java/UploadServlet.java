// Import required java libraries
import java.io.*;
import java.util.*;
import org.apache.log4j.Logger;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class UploadServlet extends HttpServlet {
    private final Logger logger = Logger.getLogger(getClass());
    private boolean isMultipart;
    private String filePathTemp;
    private File file ;
    public void init( ){
        // Get the file location where it would be stored.
        logger.info("created for test by marui");
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
        ServletFileUpload upload = new ServletFileUpload(factory);
        try{
            // Parse the request to get file items.
            List fileItems = upload.parseRequest(request);
            logger.info(fileItems);
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
                FileItem fileItem = (FileItem)i.next();
                //fileItem is a warehouse name
                if (fileItem.isFormField () && fileItem.getString().equals("on"))
                {
                    String str = filePathTemp + fileItem.getFieldName() + "\\";
                    filePath.add(str);
                    pathCount ++;
                }
                //fileItem is a module name
                else if(fileItem.isFormField () && fileItem.getFieldName().equals("module"))
                {
                    if(pathCount == 0)
                    {
                        throw new RuntimeException("you need choose at least one warehouse!");
                    }
                    for(int j = 0; j < pathCount; j++)
                    {
                        filePath.set(j,filePath.get(j)+fileItem.getString()+"\\");
                    }
                }
                //fileItem is a file
                else if(!fileItem.isFormField ())
                {
                    fileExits = true;
                    // Get the uploaded file parameters
                    String fileName = fileItem.getName();
                    boolean isInMemory = fileItem.isInMemory();
                    long sizeInBytes = fileItem.getSize();
                    logger.info("write file "+fileName+" size "+sizeInBytes);
                    for(int k = 0; k < pathCount; k++)
                    {
                        file = new File( filePath.get(k) +
                                fileName.substring( fileName.lastIndexOf("\\") + 1)) ;
                        fileItem.write( file ) ;
                    }
                    if(isInMemory)
                    {
                        out.println("Replaced File:" + fileName + ", "+ sizeInBytes + "<br>");
                    }
                    else
                    {
                        out.println("Added File:" + fileName + ", "+ sizeInBytes + "<br>");
                    }
                }
            }

            if(!fileExits)
            {
                throw new RuntimeException("you need to choose at least one file");
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