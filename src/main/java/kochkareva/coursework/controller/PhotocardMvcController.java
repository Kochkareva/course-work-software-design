package kochkareva.coursework.controller;

import jakarta.validation.Valid;
import kochkareva.coursework.model.AlbumModel;
import kochkareva.coursework.model.PhotocardModel;
import kochkareva.coursework.modelDTO.AlbumDTO;
import kochkareva.coursework.modelDTO.PhotocardDTO;
import kochkareva.coursework.service.AlbumService;
import kochkareva.coursework.service.PhotocardService;
import kochkareva.coursework.service.SessionService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/photocard")
public class PhotocardMvcController {
    private final PhotocardService photocardService;
    private final AlbumService albumService;
    private SessionService sessionService;
    private String imageBytes;
    public PhotocardMvcController(PhotocardService photocardService, SessionService sessionService,
                                  AlbumService albumService) {
        this.photocardService = photocardService;
        this.sessionService = sessionService;
        this.albumService = albumService;
    }

    @GetMapping("/all")
    public String getDepartments(Model model) throws Exception {
        model.addAttribute("photocards",
                photocardService.findAllPhotocard().stream()
                        .map(PhotocardDTO::new)
                        .collect(Collectors.toList()));
        return "userPhotocards";
    }

    @GetMapping("/userPhotocards")
    public String getUserPhotocards(Model model) throws Exception {
        model.addAttribute("photocards",
                photocardService.findPhotocardsByUser(Integer.parseInt(sessionService.getIdUser())).stream()
                        .map(PhotocardDTO::new)
                        .collect(Collectors.toList()));
        return "userPhotocards";
    }

    @GetMapping(value = "/create")
    public String createPhotocard(
            @ModelAttribute("photocardDTO") @Valid PhotocardDTO photocardDTO,
            @ModelAttribute("albumDTO") @Valid AlbumDTO albumDTO,
            BindingResult bindingResult,
            Model model) {
        PhotocardDTO photocard = new PhotocardDTO();
        photocard.setImage(new byte[0]);

        model.addAttribute("albumDTO",
                albumService.findAlbumsByUser(Integer.parseInt(sessionService.getIdUser())).stream()
                        .map(AlbumDTO::new)
                        .collect(Collectors.toList()));
        model.addAttribute("photocardDTO", photocard);
        model.addAttribute("photocardDTO", photocard);
        //return ResponseEntity.ok(photocardDTO);
        return "createPhotocard";
    }

    @PostMapping(value = "/create", consumes="multipart/form-data")
    public String savePhotocard(
                                @ModelAttribute @Valid PhotocardDTO photocardDTO,
                                @RequestParam("imageFile") MultipartFile image,
                                BindingResult bindingResult,
                                Model model) throws IOException {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            System.out.println(bindingResult.getAllErrors());
            return "createPhotocard";
        }
        //byte[] imageBytes = image.getBytes();
        BufferedImage bufferedImage = ImageIO.read(image.getInputStream());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", baos);
        byte[] imageBytes = baos.toByteArray();
        photocardDTO.setImage(imageBytes);
        photocardDTO.setIdCreator(Integer.parseInt(sessionService.getIdUser()));
       // photocardDTO.setIdAlbum(1);
        photocardService.addPhotocard(photocardDTO);
        return "redirect:/photocard/userPhotocards";
    }

    @GetMapping(value = "/image/{id}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getImage(@PathVariable("id") String id) throws Exception {
        // Ваш код для создания изображения на основе id
        byte[] imageBytes = photocardService.findById(Integer.parseInt(id)).getImage();// Ваши байты изображения

        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(imageBytes);
    }

    @PostMapping("/delete/{id}")
    public String deletePhotocard(@PathVariable Long id) {
        photocardService.deletePhotocard(id.intValue());
        return "redirect:/photocard/userPhotocards";
    }
}
