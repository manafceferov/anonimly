package com.anonimly.controller;

import com.anonimly.dto.auth.LoginRequestDto;
import com.anonimly.dto.auth.LoginResponseDto;
import com.anonimly.dto.comment.CommentCreateDto;
import com.anonimly.dto.post.PostCreateDto;
import com.anonimly.dto.post.PostDetailResponseDto;
import com.anonimly.dto.post.PostResponseDto;
import com.anonimly.dto.user.UserRegisterDto;
import com.anonimly.service.AuthService;
import com.anonimly.service.CommentService;
import com.anonimly.service.LikeService;
import com.anonimly.service.PostService;
import com.anonimly.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class MvcController {

    private final PostService postService;
    private final AuthService authService;
    private final UserService userService;
    private final CommentService commentService;
    private final LikeService likeService;

    public MvcController(PostService postService,
                         AuthService authService,
                         UserService userService,
                         CommentService commentService,
                         LikeService likeService
    ) {
        this.postService = postService;
        this.authService = authService;
        this.userService = userService;
        this.commentService = commentService;
        this.likeService = likeService;
    }

    @GetMapping("/")
    public String home(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<PostResponseDto> posts = postService.getAll(PageRequest.of(page, 10));
        model.addAttribute("posts", posts.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", posts.getTotalPages());
        return "home";
    }

    @GetMapping("/web/posts/{slug}")
    public String postDetail(@PathVariable String slug, Model model, HttpSession session) {
        PostDetailResponseDto post = postService.getBySlug(slug);
        model.addAttribute("post", post);
        model.addAttribute("comments", commentService.getByPost(post.getId(), PageRequest.of(0, 20)).getContent());
        model.addAttribute("userId", session.getAttribute("userId"));
        return "post-detail";
    }

    @GetMapping("/web/posts/new")
    public String newPostPage(HttpSession session) {
        System.out.println("==> /web/posts/new səhifəsinə giriş cəhdi!");
        System.out.println("==> Mövcud Session daxilindəki UserId: " + session.getAttribute("userId"));
        if (session.getAttribute("userId") == null) {
            System.out.println("==> UserId NULL-dur! Logine yönləndirilir...");
            return "redirect:/login";
        }
        return "post-new";
    }

    @PostMapping("/web/posts/new")
    public String createPost(@RequestParam String title,
                             @RequestParam String content,
                             @RequestParam(defaultValue = "true") boolean published,
                             HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        PostCreateDto dto = new PostCreateDto();
        dto.setTitle(title);
        dto.setContent(content);
        dto.setPublished(published);
        var post = postService.create(dto, userId);
        return "redirect:/web/posts/" + post.getSlug();
    }

    @GetMapping("/web/posts/{slug}/like")
    public String like(@PathVariable String slug, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";
        PostDetailResponseDto post = postService.getBySlug(slug);
        likeService.like(post.getId(), userId);
        return "redirect:/web/posts/" + slug;
    }

    @GetMapping("/web/posts/{slug}/dislike")
    public String dislike(@PathVariable String slug, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";
        PostDetailResponseDto post = postService.getBySlug(slug);
        likeService.dislike(post.getId(), userId);
        return "redirect:/web/posts/" + slug;
    }

    @PostMapping("/web/posts/{slug}/comment")
    public String comment(@PathVariable String slug,
                          @RequestParam String content,
                          HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";
        PostDetailResponseDto post = postService.getBySlug(slug);
        CommentCreateDto dto = new CommentCreateDto();
        dto.setContent(content);
        dto.setPostId(post.getId());
        commentService.create(dto, userId);
        return "redirect:/web/posts/" + slug;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        try {
            System.out.println("==> Loqin istəyi gəldi: " + username);
            LoginRequestDto dto = new LoginRequestDto();
            dto.setUsername(username);
            dto.setPassword(password);
            LoginResponseDto response = authService.login(dto);
            System.out.println("==> Service-dən gələn UserId: " + response.getUserId());
            session.setAttribute("userId", response.getUserId());
            session.setAttribute("username", response.getUsername());
            session.setAttribute("token", response.getToken());
            System.out.println("==> Session-a yazılan UserId: " + session.getAttribute("userId"));
            return "redirect:/";
        } catch (Exception e) {
            System.out.println("==> Loqin xətası: " + e.getMessage());
            model.addAttribute("error", "İstifadəçi adı və ya şifrə yanlışdır");
            return "login";
        }
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String email,
                           @RequestParam String password,
                           Model model
    ) {
        try {
            UserRegisterDto dto = new UserRegisterDto();
            dto.setUsername(username);
            dto.setEmail(email);
            dto.setPassword(password);
            userService.register(dto);
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}