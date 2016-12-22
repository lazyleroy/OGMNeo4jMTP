package config;

/**
 * Created by Felix Hambrecht on 27.07.2016.
 */


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
import requestAnswers.SimpleAnswer;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.CharArrayWriter;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * This class handles the FileUpload. It is part of the RESTful WebService.
 */
@Controller
@MultipartConfig(maxFileSize = 50)
public class FileUploadController implements HandlerExceptionResolver {

    private static final Logger log = LoggerFactory.getLogger(FileUploadController.class);


    public static final String ROOT = "var/www/html/pics/";

    private final ResourceLoader resourceLoader;

    DatabaseOperations db = new DatabaseOperations();

    @Autowired
    public FileUploadController(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/uploadFile")
    public @ResponseBody SimpleAnswer handleFileUpload(@RequestParam("file") MultipartFile file, @RequestHeader(value="accessToken")
            String accessToken) {
        return db.uploadProfilePicture(file, accessToken);
    }


    @ExceptionHandler(MultipartException.class)
    public SimpleAnswer resolveMultipartException (Exception e){
        return new SimpleAnswer(false,"problems");
    }

    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                         Object o, Exception e) {

        ModelAndView mav = new ModelAndView(new MappingJackson2JsonView());
        if (e instanceof MultipartException){
            mav.addObject(new SimpleAnswer(false, "Filesize too large"));
            e.printStackTrace();
            return mav;
        }

        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        System.out.println("ACHTUNG");
        System.out.println(sw.toString());
        mav.addObject(new SimpleAnswer(false, sw.toString()));
        return mav;
    }

}
