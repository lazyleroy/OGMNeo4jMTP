package config;

/**
 * Created by Felix on 27.07.2016.
 */
//import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
//import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import org.apache.tomcat.util.http.fileupload.FileUploadBase;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
import requestAnswers.SimpleAnswer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static javax.servlet.http.HttpServletResponse.SC_BAD_GATEWAY;
import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

@Controller
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
    public @ResponseBody SimpleAnswer handleFileUpload(@RequestParam("file") MultipartFile file, @RequestParam("accessToken")
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
            return mav;
        }
        mav.addObject(new SimpleAnswer(false));
        return mav;
    }

}
