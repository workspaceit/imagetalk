package controller.service;

/**
 * Created by mi on 9/30/15.
 */

// Import required java libraries
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
//import org.apache.commons.io.output.*;

public class UploadImage extends HttpServlet {

    private boolean isMultipart;
    private String filePath;
    private int maxFileSize = 1024 * 1024;
    private int maxMemSize = 1024 * 1024;
    private File file ;

    public void init( ){
        // Get the file location where it would be stored.
        filePath =
                getServletContext().getInitParameter("file-upload");
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //processRequest(request, response);

        String s = request.getParameter("img");
        System.out.println(s);
        File file = new File("/home/mi/pic/a.jpg") ;
        ImageIO.write(decodeToImage(s),"jpg",file);
    }

    private void writeBtesToFile(byte[] bytes, File file) {
        OutputStream out;

        try {
            out = new FileOutputStream(file);
            out.write(bytes);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
    }

    public static BufferedImage decodeToImage(String imageString)
    {
        BufferedImage image = null;
        byte[] imageByte;
        try
        {
            BASE64Decoder decoder = new BASE64Decoder();
            imageByte = decoder.decodeBuffer(imageString);
            ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
            image = ImageIO.read(bis);
            bis.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return image;
    }

    public static String encodeToString(BufferedImage image, String type)
    {
        String imageString = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try
        {
            ImageIO.write(image, type, bos);
            byte[] imageBytes = bos.toByteArray();

            BASE64Encoder encoder = new BASE64Encoder();
            imageString = encoder.encode(imageBytes);

            bos.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return imageString;
    }
//    public void doPost(HttpServletRequest request,
//                       HttpServletResponse response)
//            throws ServletException, java.io.IOException {
//        // Check that we have a file upload request
//        isMultipart = ServletFileUpload.isMultipartContent(request);
//        response.setContentType("text/html");
//        java.io.PrintWriter out = response.getWriter( );
//        if( !isMultipart ){
//            out.println("<html>");
//            out.println("<head>");
//            out.println("<title>Servlet upload</title>");
//            out.println("</head>");
//            out.println("<body>");
//            out.println("<p>No file uploaded</p>");
//            out.println("</body>");
//            out.println("</html>");
//            return;
//        }
//        DiskFileItemFactory factory = new DiskFileItemFactory();
//        // maximum size that will be stored in memory
//        factory.setSizeThreshold(maxMemSize);
//        // Location to save data that is larger than maxMemSize.
//        String appPath = request.getServletContext().getRealPath("");
//        // constructs path of the directory to save uploaded file
//        String savePath = appPath + File.separator;
//        filePath = savePath;
//        savePath = "/home/mi/pic/";
//        filePath = savePath;
//        factory.setRepository(new File(savePath));
//        System.out.println(savePath);
//        System.out.println(filePath);
//        // Create a new file upload handler
//        ServletFileUpload upload = new ServletFileUpload(factory);
//        // maximum file size to be uploaded.
//        upload.setSizeMax( maxFileSize );
//
//        try{
//            // Parse the request to get file items.
//            List fileItems = upload.parseRequest(request);
//
//            // Process the uploaded file items
//            Iterator i = fileItems.iterator();
//
//            out.println("<html>");
//            out.println("<head>");
//            out.println("<title>Servlet upload</title>");
//            out.println("</head>");
//            out.println("<body>");
//            while ( i.hasNext () )
//            {
//                FileItem fi = (FileItem)i.next();
//                if ( !fi.isFormField () )
//                {
//                    // Get the uploaded file parameters
//                    String fieldName = fi.getFieldName();
//                    String fileName = fi.getName();
//                    String contentType = fi.getContentType();
//                    boolean isInMemory = fi.isInMemory();
//                    long sizeInBytes = fi.getSize();
//                    // Write the file
//                    if( fileName.lastIndexOf("\\") >= 0 ){
//                        file = new File( filePath +
//                                fileName.substring( fileName.lastIndexOf("\\"))) ;
//                    }else{
//                        file = new File( filePath +
//                                fileName.substring(fileName.lastIndexOf("\\")+1)) ;
//                    }
//                    fi.write( file ) ;
//                    out.println("Uploaded Filename: " + fileName + "<br>");
//                }
//            }
//            out.println("</body>");
//            out.println("</html>");
//        }catch(Exception ex) {
//            System.out.println(ex);
//        }
//    }
//    public void doGet(HttpServletRequest request,
//                      HttpServletResponse response)
//            throws ServletException, java.io.IOException {
//
//        throw new ServletException("GET method used with " +
//                getClass( ).getName( )+": POST method required.");
//    }
}