package com.anonimly.controller;

import com.anonimly.dto.auth.LoginRequestDto;
import com.anonimly.dto.auth.LoginResponseDto;
import com.anonimly.dto.comment.CommentCreateDto;
import com.anonimly.dto.comment.CommentEditDto;
import com.anonimly.dto.comment.CommentResponseDto;
import com.anonimly.dto.post.PostCreateDto;
import com.anonimly.dto.post.PostDetailResponseDto;
import com.anonimly.dto.post.PostEditDto;
import com.anonimly.dto.post.PostResponseDto;
import com.anonimly.dto.user.UserEditDto;
import com.anonimly.dto.user.UserRegisterDto;
import com.anonimly.dto.user.UserResponseDto;
import com.anonimly.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class MvcController {

    private final PostService postService;
    private final AuthService authService;
    private final UserService userService;
    private final CommentService commentService;
    private final LikeService likeService;
    private final CommentLikeService commentLikeService;

    public MvcController(PostService postService,
                         AuthService authService,
                         UserService userService,
                         CommentService commentService,
                         LikeService likeService,
                         CommentLikeService commentLikeService
    ) {
        this.postService = postService;
        this.authService = authService;
        this.userService = userService;
        this.commentService = commentService;
        this.likeService = likeService;
        this.commentLikeService = commentLikeService;
    }

    @GetMapping("/")
    public String home(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(required = false) String search,
                       Model model
    ) {

        PageRequest pageRequest = PageRequest.of(
                page,
                10,
                Sort.by(Sort.Direction.DESC, "createdAt", "id")
        );

        Page<PostResponseDto> posts;

        if (search != null && !search.isBlank()) {
            posts = postService.search(search, pageRequest);
            model.addAttribute("search", search);
        } else {
            posts = postService.getAll(pageRequest);
        }

        model.addAttribute("posts", posts.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", posts.getTotalPages());

        return "home";
    }

    @GetMapping("/web/posts/{slug}")
    public String postDetail(@PathVariable String slug,
                             Model model,
                             HttpSession session
    ) {
        PostDetailResponseDto post = postService.getBySlug(slug);
        model.addAttribute("post", post);
        model.addAttribute("comments", commentService.getByPost(post.getId(), PageRequest.of(0, 20)).getContent());
        model.addAttribute("userId", session.getAttribute("userId"));
        return "post-detail";
    }

    @GetMapping("/web/posts/new")
    public String newPostPage(HttpSession session) {
        if (session.getAttribute("userId") == null) return "redirect:/login";
        return "post-new";
    }

    @PostMapping("/web/posts/new")
    public String createPost(@RequestParam String title,
                             @RequestParam String content,
                             @RequestParam(defaultValue = "true") boolean published,
                             HttpSession session
    ) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        PostCreateDto dto = new PostCreateDto();
        dto.setTitle(title);
        dto.setContent(content);
        dto.setPublished(published);
        var post = postService.create(dto, userId);
        return "redirect:/web/posts/" + post.getSlug();
    }

    @PostMapping("/web/posts/{slug}/edit")
    public String editPost(@PathVariable String slug,
                           @RequestParam String title,
                           HttpSession session
    ) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        PostDetailResponseDto post = postService.getBySlug(slug);
        PostEditDto dto = new PostEditDto();
        dto.setTitle(title);
        postService.edit(post.getId(), dto, userId);
        return "redirect:/web/profile";
    }

    @PostMapping("/web/posts/{slug}/delete")
    public String deletePost(@PathVariable String slug,
                             HttpSession session
    ) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";
        PostDetailResponseDto post = postService.getBySlug(slug);
        postService.delete(post.getId(), userId);
        return "redirect:/web/profile";
    }

    @GetMapping("/web/posts/{slug}/like")
    @ResponseBody
    public String like(@PathVariable String slug,
                       HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "LOGIN";
        PostDetailResponseDto post = postService.getBySlug(slug);
        likeService.like(post.getId(), userId);
        return "OK";
    }

    @GetMapping("/web/posts/{slug}/dislike")
    @ResponseBody
    public String dislike(@PathVariable String slug,
                          HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "LOGIN";

        PostDetailResponseDto post = postService.getBySlug(slug);
        likeService.dislike(post.getId(), userId);

        return "OK";
    }

    @PostMapping("/web/comments/{id}/like")
    @ResponseBody
    public String likeComment(@PathVariable Long id, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "LOGIN";
        return commentLikeService.like(id, userId);
    }

    @PostMapping("/web/posts/{slug}/comment")
    public String comment(@PathVariable String slug,
                          @RequestParam String content,
                          HttpSession session
    ) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";
        PostDetailResponseDto post = postService.getBySlug(slug);
        CommentCreateDto dto = new CommentCreateDto();
        dto.setContent(content);
        dto.setPostId(post.getId());
        commentService.create(dto, userId);
        return "redirect:/web/posts/" + slug;
    }

    @PostMapping("/web/comments/{id}/edit")
    public String editComment(@PathVariable Long id,
                              @RequestParam String content,
                              HttpSession session
    ) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        CommentEditDto dto = new CommentEditDto();
        dto.setContent(content);
        commentService.edit(id, dto, userId);
        return "redirect:/web/profile";
    }

    @GetMapping("/web/profile")
    public String profile(HttpSession session,
                          Model model
    ) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";
        UserResponseDto user = userService.getById(userId);
        List<PostResponseDto> posts = postService.getByUserId(userId, PageRequest.of(0, 20)).getContent();
        List<CommentResponseDto> comments = commentService.getByUserId(userId, PageRequest.of(0, 20)).getContent();
        model.addAttribute("user", user);
        model.addAttribute("posts", posts);
        model.addAttribute("comments", comments);
        return "profile";
    }

    @PostMapping("/web/profile/edit")
    public String editProfile(@RequestParam(required = false) String username,
                              @RequestParam(required = false) String bio,
                              @RequestParam(required = false) String avatarUrl,
                              HttpSession session
    ) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        try {
            UserEditDto dto = new UserEditDto();
            dto.setUsername(username);
            dto.setBio(bio);
            dto.setAvatarUrl(avatarUrl);
            UserResponseDto updated = userService.edit(userId, dto);
            session.setAttribute("username", updated.getUsername());
        } catch (Exception e) {
        }
        return "redirect:/web/profile";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model
    ) {
        try {
            LoginRequestDto dto = new LoginRequestDto();
            dto.setUsername(username);
            dto.setPassword(password);
            LoginResponseDto response = authService.login(dto);
            session.setAttribute("userId", response.getUserId());
            session.setAttribute("username", response.getUsername());
            session.setAttribute("token", response.getToken());
            return "redirect:/";
        } catch (Exception e) {
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