package com.aiyowei.weibao.service;

import com.aiyowei.weibao.dto.AdminStatisticDto;
import com.aiyowei.weibao.dto.CartItemDto;
import com.aiyowei.weibao.dto.CartItemRequest;
import com.aiyowei.weibao.dto.CategoryDto;
import com.aiyowei.weibao.dto.CategoryRequest;
import com.aiyowei.weibao.dto.CoinHistoryDto;
import com.aiyowei.weibao.dto.CoinTaskDto;
import com.aiyowei.weibao.dto.DishDto;
import com.aiyowei.weibao.dto.DishRequest;
import com.aiyowei.weibao.dto.FamilyInfoDto;
import com.aiyowei.weibao.dto.FamilyMemberDto;
import com.aiyowei.weibao.dto.LoginResponse;
import com.aiyowei.weibao.dto.OrderDetailDto;
import com.aiyowei.weibao.dto.OrderPreviewResponse;
import com.aiyowei.weibao.dto.OrderSummaryDto;
import com.aiyowei.weibao.dto.UserProfileDto;
import com.aiyowei.weibao.dto.WalletDto;
import com.aiyowei.weibao.entity.CartItem;
import com.aiyowei.weibao.entity.Category;
import com.aiyowei.weibao.entity.CoinRewardLog;
import com.aiyowei.weibao.entity.CoinTask;
import com.aiyowei.weibao.entity.CoinTransaction;
import com.aiyowei.weibao.entity.CoinWallet;
import com.aiyowei.weibao.entity.Dish;
import com.aiyowei.weibao.entity.Family;
import com.aiyowei.weibao.entity.FamilyMember;
import com.aiyowei.weibao.entity.OrderItem;
import com.aiyowei.weibao.entity.Orders;
import com.aiyowei.weibao.entity.User;
import com.aiyowei.weibao.mapper.CartItemMapper;
import com.aiyowei.weibao.mapper.CategoryMapper;
import com.aiyowei.weibao.mapper.CoinRewardLogMapper;
import com.aiyowei.weibao.mapper.CoinTaskMapper;
import com.aiyowei.weibao.mapper.CoinTransactionMapper;
import com.aiyowei.weibao.mapper.CoinWalletMapper;
import com.aiyowei.weibao.mapper.DishMapper;
import com.aiyowei.weibao.mapper.FamilyMapper;
import com.aiyowei.weibao.mapper.FamilyMemberMapper;
import com.aiyowei.weibao.mapper.OrderItemMapper;
import com.aiyowei.weibao.mapper.OrdersMapper;
import com.aiyowei.weibao.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class MockDataService {

    private static final ZoneOffset ZONE_OFFSET = ZoneOffset.ofHours(8);
    private final AtomicLong orderIdGenerator = new AtomicLong(1000);

    private final UserMapper userMapper;
    private final FamilyMapper familyMapper;
    private final FamilyMemberMapper familyMemberMapper;
    private final CategoryMapper categoryMapper;
    private final DishMapper dishMapper;
    private final CartItemMapper cartItemMapper;
    private final OrdersMapper ordersMapper;
    private final OrderItemMapper orderItemMapper;
    private final CoinWalletMapper coinWalletMapper;
    private final CoinTransactionMapper coinTransactionMapper;
    private final CoinTaskMapper coinTaskMapper;
    private final CoinRewardLogMapper coinRewardLogMapper;

    public LoginResponse login(String nickname) {
        User user = ensureCurrentUser(nickname);
        FamilyInfoDto familyInfo = getFamilyInfo();
        WalletDto wallet = getWallet();
        return new LoginResponse(
            "",
            new UserProfileDto(user.getId(), user.getNickname(), user.getAvatar()),
            familyInfo,
            wallet
        );
    }

    public LoginResponse profile(Long userId) {
        LoginResponse response = login(null);
        if (userId != null) {
            User user = userMapper.selectById(userId);
            if (user != null) {
                response.setUser(new UserProfileDto(user.getId(), user.getNickname(), user.getAvatar()));
            }
        }
        return response;
    }

    public FamilyInfoDto getFamilyInfo() {
        Long userId = currentUserId();
        FamilyMember membership = familyMemberMapper.selectOne(
            new LambdaQueryWrapper<FamilyMember>().eq(FamilyMember::getUserId, userId).last("limit 1")
        );
        if (membership == null) {
            createDefaultFamily(ensureCurrentUser(null));
            membership = familyMemberMapper.selectOne(
                new LambdaQueryWrapper<FamilyMember>().eq(FamilyMember::getUserId, userId).last("limit 1")
            );
        }
        Family family = familyMapper.selectById(membership.getFamilyId());
        List<FamilyMember> members = familyMemberMapper.selectList(
            new LambdaQueryWrapper<FamilyMember>().eq(FamilyMember::getFamilyId, family.getId())
        );
        List<Long> userIds = members.stream().map(FamilyMember::getUserId).collect(Collectors.toList());
        Map<Long, User> userMap;
        if (CollectionUtils.isEmpty(userIds)) {
            userMap = new HashMap<>();
        } else {
            userMap = userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
        }
        List<FamilyMemberDto> memberDtos = members.stream()
            .map(item -> new FamilyMemberDto(
                item.getUserId(),
                userMap.getOrDefault(item.getUserId(), new User()).getNickname(),
                item.getRole()
            ))
            .collect(Collectors.toList());
        return new FamilyInfoDto(family.getId(), family.getName(), family.getType(), memberDtos);
    }

    public WalletDto getWallet() {
        CoinWallet wallet = ensureWallet(currentFamilyId());
        return new WalletDto(wallet.getBalance(), wallet.getFrozen());
    }

    public List<CategoryDto> getCategories() {
        return categoryMapper.selectList(new LambdaQueryWrapper<Category>()
                .eq(Category::getVisible, 1)
                .orderByAsc(Category::getSortOrder))
            .stream()
            .map(category -> new CategoryDto(
                category.getId(),
                category.getName(),
                category.getIcon(),
                category.getDescription(),
                category.getSortOrder(),
                category.getVisible() != null && category.getVisible() == 1
            ))
            .collect(Collectors.toList());
    }

    public List<DishDto> getDishes(Long categoryId, String keyword) {
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<Dish>()
            .eq(Dish::getStatus, 1);
        if (categoryId != null) {
            wrapper.eq(Dish::getCategoryId, categoryId);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Dish::getName, keyword);
        }
        return dishMapper.selectList(wrapper.orderByAsc(Dish::getId))
            .stream()
            .map(this::toDishDto)
            .collect(Collectors.toList());
    }

    public Optional<DishDto> getDish(Long id) {
        Dish dish = dishMapper.selectById(id);
        return Optional.ofNullable(dish).map(this::toDishDto);
    }

    public CategoryDto createCategory(CategoryRequest request) {
        Category category = new Category();
        category.setName(request.getName());
        category.setIcon(request.getIcon());
        category.setDescription(request.getDescription());
        category.setSortOrder(request.getSortOrder());
        category.setVisible(Boolean.TRUE.equals(request.getVisible()) ? 1 : 0);
        categoryMapper.insert(category);
        return toCategoryDto(category);
    }

    public CategoryDto updateCategory(Long id, CategoryRequest request) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new IllegalArgumentException("分类不存在");
        }
        category.setName(request.getName());
        category.setIcon(request.getIcon());
        category.setDescription(request.getDescription());
        category.setSortOrder(request.getSortOrder());
        if (request.getVisible() != null) {
            category.setVisible(request.getVisible() ? 1 : 0);
        }
        categoryMapper.updateById(category);
        return toCategoryDto(category);
    }

    public void deleteCategory(Long id) {
        categoryMapper.deleteById(id);
    }

    public DishDto createDish(DishRequest request) {
        Dish dish = new Dish();
        applyDish(request, dish);
        dishMapper.insert(dish);
        return toDishDto(dish);
    }

    public DishDto updateDish(Long id, DishRequest request) {
        Dish dish = dishMapper.selectById(id);
        if (dish == null) {
            throw new IllegalArgumentException("菜品不存在");
        }
        applyDish(request, dish);
        dishMapper.updateById(dish);
        return toDishDto(dish);
    }

    public void deleteDish(Long id) {
        dishMapper.deleteById(id);
    }

    public List<CartItemDto> getCart(Long familyId) {
        List<CartItem> items = cartItemMapper.selectList(
            new LambdaQueryWrapper<CartItem>().eq(CartItem::getFamilyId, familyId).orderByAsc(CartItem::getId)
        );
        if (items.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> dishIds = items.stream().map(CartItem::getDishId).collect(Collectors.toList());
        Map<Long, Dish> dishMap = dishMapper.selectBatchIds(dishIds).stream()
            .collect(Collectors.toMap(Dish::getId, Function.identity()));
        return items.stream()
            .map(item -> toCartItemDto(item, dishMap.get(item.getDishId())))
            .collect(Collectors.toList());
    }

    @Transactional
    public List<CartItemDto> upsertCartItem(Long familyId, CartItemRequest request) {
        Dish dish = dishMapper.selectById(request.getDishId());
        if (dish == null) {
            throw new IllegalArgumentException("菜品不存在");
        }
        CartItem existing = cartItemMapper.selectOne(new LambdaQueryWrapper<CartItem>()
            .eq(CartItem::getFamilyId, familyId)
            .eq(CartItem::getDishId, request.getDishId())
            .last("limit 1"));
        if (existing == null) {
            CartItem item = new CartItem();
            item.setFamilyId(familyId);
            item.setDishId(request.getDishId());
            item.setQuantity(request.getQuantity());
            item.setNote(request.getNote());
            cartItemMapper.insert(item);
        } else {
            existing.setQuantity(request.getQuantity());
            existing.setNote(request.getNote());
            cartItemMapper.updateById(existing);
        }
        return getCart(familyId);
    }

    public List<CartItemDto> removeCartItem(Long familyId, Long dishId) {
        cartItemMapper.delete(new LambdaQueryWrapper<CartItem>()
            .eq(CartItem::getFamilyId, familyId)
            .eq(CartItem::getDishId, dishId));
        return getCart(familyId);
    }

    public OrderPreviewResponse previewOrder(Long familyId) {
        List<CartItemDto> items = getCart(familyId);
        double total = items.stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();
        double discount = Math.min(20, total * 0.1);
        CoinWallet wallet = ensureWallet(familyId);
        int coinAvailable = wallet.getBalance();
        int recommendedCoin = (int) Math.min(coinAvailable, Math.floor(discount * 10));
        double payable = total - (recommendedCoin / 100.0);
        return new OrderPreviewResponse(total, discount, coinAvailable, recommendedCoin, payable, items);
    }

    @Transactional
    public OrderDetailDto submitOrder(Long familyId, Integer coinUse) {
        OrderPreviewResponse preview = previewOrder(familyId);
        CoinWallet wallet = ensureWallet(familyId);
        int useCoin = coinUse == null ? preview.getCoinRecommended() : Math.min(coinUse, wallet.getBalance());
        wallet.setBalance(wallet.getBalance() - useCoin);
        coinWalletMapper.updateById(wallet);

        double payAmountValue = preview.getTotalAmount() - (useCoin / 100.0);
        Orders order = new Orders();
        order.setFamilyId(familyId);
        order.setOrderNo(generateOrderNo());
        order.setTotalAmount(BigDecimal.valueOf(preview.getTotalAmount()));
        order.setDiscountAmount(BigDecimal.valueOf(preview.getDiscountAmount()));
        order.setCoinUsed(useCoin);
        order.setPayAmount(BigDecimal.valueOf(payAmountValue));
        order.setStatus("pending");
        order.setDeliveryType("home");
        order.setRemark("情侣点餐");
        ordersMapper.insert(order);

        List<CartItemDto> items = getCart(familyId);
        for (CartItemDto dto : items) {
            OrderItem item = new OrderItem();
            item.setOrderId(order.getId());
            item.setDishId(dto.getDishId());
            item.setName(dto.getDishName());
            item.setPrice(BigDecimal.valueOf(dto.getPrice()));
            item.setQuantity(dto.getQuantity());
            item.setNote(dto.getNote());
            orderItemMapper.insert(item);
        }

        cartItemMapper.delete(new LambdaQueryWrapper<CartItem>().eq(CartItem::getFamilyId, familyId));

        CoinTransaction transaction = new CoinTransaction();
        transaction.setWalletId(wallet.getId());
        transaction.setType("pay");
        transaction.setAmount(-useCoin);
        transaction.setOrderId(order.getId());
        transaction.setRemark("订单抵扣 " + order.getOrderNo());
        coinTransactionMapper.insert(transaction);

        return new OrderDetailDto(
            order.getId(),
            order.getStatus(),
            order.getTotalAmount().doubleValue(),
            order.getCoinUsed(),
            order.getPayAmount().doubleValue(),
            order.getDeliveryType(),
            toOffset(order.getCreatedAt()),
            items
        );
    }

    public Optional<OrderDetailDto> getOrder(Long id) {
        Orders order = ordersMapper.selectById(id);
        if (order == null) {
            return Optional.empty();
        }
        List<OrderItem> orderItems = orderItemMapper.selectList(
            new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, order.getId())
        );
        List<CartItemDto> items = orderItems.stream()
            .map(item -> new CartItemDto(
                item.getDishId(),
                item.getName(),
                item.getQuantity(),
                item.getNote(),
                item.getPrice().doubleValue()
            ))
            .collect(Collectors.toList());
        OrderDetailDto dto = new OrderDetailDto(
            order.getId(),
            order.getStatus(),
            order.getTotalAmount().doubleValue(),
            order.getCoinUsed(),
            order.getPayAmount().doubleValue(),
            order.getDeliveryType(),
            toOffset(order.getCreatedAt()),
            items
        );
        return Optional.of(dto);
    }

    public List<OrderSummaryDto> getFamilyOrders(Long familyId) {
        return ordersMapper.selectList(
                new LambdaQueryWrapper<Orders>().eq(Orders::getFamilyId, familyId).orderByDesc(Orders::getCreatedAt)
            ).stream()
            .map(order -> new OrderSummaryDto(
                order.getId(),
                order.getStatus(),
                order.getTotalAmount().doubleValue(),
                order.getCoinUsed(),
                order.getPayAmount().doubleValue(),
                toOffset(order.getCreatedAt())
            ))
            .collect(Collectors.toList());
    }

    public List<CoinTaskDto> getTasks() {
        CoinWallet wallet = ensureWallet(currentFamilyId());
        List<CoinRewardLog> logs = coinRewardLogMapper.selectList(
            new LambdaQueryWrapper<CoinRewardLog>().eq(CoinRewardLog::getWalletId, wallet.getId())
        );
        Map<String, CoinRewardLog> logMap = logs.stream().collect(Collectors.toMap(CoinRewardLog::getTaskCode, Function.identity()));
        return coinTaskMapper.selectList(new LambdaQueryWrapper<CoinTask>().eq(CoinTask::getStatus, 1))
            .stream()
            .map(task -> new CoinTaskDto(
                task.getCode(),
                task.getTitle(),
                task.getDescription(),
                task.getReward(),
                task.getLimitType(),
                logMap.containsKey(task.getCode())
            ))
            .collect(Collectors.toList());
    }

    public List<CoinHistoryDto> getHistories() {
        CoinWallet wallet = ensureWallet(currentFamilyId());
        return coinTransactionMapper.selectList(
                new LambdaQueryWrapper<CoinTransaction>().eq(CoinTransaction::getWalletId, wallet.getId()).orderByDesc(CoinTransaction::getCreatedAt)
            ).stream()
            .map(tx -> new CoinHistoryDto(
                tx.getType(),
                tx.getAmount(),
                tx.getRemark(),
                toOffset(tx.getCreatedAt())
            ))
            .collect(Collectors.toList());
    }

    @Transactional
    public WalletDto claimTask(String taskCode) {
        CoinTask task = coinTaskMapper.selectOne(new LambdaQueryWrapper<CoinTask>().eq(CoinTask::getCode, taskCode));
        if (task == null) {
            throw new IllegalArgumentException("任务不存在");
        }
        CoinWallet wallet = ensureWallet(currentFamilyId());
        CoinRewardLog log = coinRewardLogMapper.selectOne(new LambdaQueryWrapper<CoinRewardLog>()
            .eq(CoinRewardLog::getWalletId, wallet.getId())
            .eq(CoinRewardLog::getTaskCode, taskCode));
        if (log != null) {
            return new WalletDto(wallet.getBalance(), wallet.getFrozen());
                }
                wallet.setBalance(wallet.getBalance() + task.getReward());
        coinWalletMapper.updateById(wallet);

        CoinRewardLog rewardLog = new CoinRewardLog();
        rewardLog.setWalletId(wallet.getId());
        rewardLog.setTaskCode(task.getCode());
        rewardLog.setAmount(task.getReward());
        coinRewardLogMapper.insert(rewardLog);

        CoinTransaction tx = new CoinTransaction();
        tx.setWalletId(wallet.getId());
        tx.setType("reward");
        tx.setAmount(task.getReward());
        tx.setTaskCode(task.getCode());
        tx.setRemark(task.getTitle());
        coinTransactionMapper.insert(tx);

        return new WalletDto(wallet.getBalance(), wallet.getFrozen());
    }

    public AdminStatisticDto getStatistics() {
        int totalOrders = ordersMapper.selectCount(null).intValue();
        double revenue = ordersMapper.selectList(null).stream()
            .map(Orders::getPayAmount)
            .filter(amount -> amount != null)
            .mapToDouble(BigDecimal::doubleValue)
            .sum();
        int coinIssued = coinTransactionMapper.selectList(new LambdaQueryWrapper<CoinTransaction>().gt(CoinTransaction::getAmount, 0))
            .stream()
            .mapToInt(CoinTransaction::getAmount)
            .sum();
        int families = familyMapper.selectCount(null).intValue();
        return new AdminStatisticDto(totalOrders, revenue, coinIssued, families);
    }

    public Map<String, Object> dashboard() {
        long dishCount = dishMapper.selectCount(null);
        long categoryCount = categoryMapper.selectCount(null);
        long orderCount = ordersMapper.selectCount(null);
        int coinBalance = coinWalletMapper.selectList(null).stream()
            .mapToInt(CoinWallet::getBalance)
            .sum();
        Map<String, Object> result = new HashMap<>();
        result.put("dishCount", dishCount);
        result.put("categoryCount", categoryCount);
        result.put("orderCount", orderCount);
        result.put("coinBalance", coinBalance);
        return result;
    }

    private User ensureCurrentUser(String nickname) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().orderByAsc(User::getId).last("limit 1"));
        if (user == null) {
            user = new User();
            user.setNickname(StringUtils.hasText(nickname) ? nickname : "情侣用户");
            user.setStatus(1);
            userMapper.insert(user);
        } else if (StringUtils.hasText(nickname) && !nickname.equals(user.getNickname())) {
            user.setNickname(nickname);
            userMapper.updateById(user);
        }
        ensureFamily(user);
        return user;
    }

    private void ensureFamily(User user) {
        FamilyMember membership = familyMemberMapper.selectOne(
            new LambdaQueryWrapper<FamilyMember>().eq(FamilyMember::getUserId, user.getId()).last("limit 1")
        );
        if (membership != null) {
            return;
        }
        createDefaultFamily(user);
    }

    private void createDefaultFamily(User user) {
        Family family = new Family();
        family.setName(user.getNickname() + "的专属餐厅");
        family.setType("couple");
        family.setOwnerId(user.getId());
        familyMapper.insert(family);

        CoinWallet wallet = new CoinWallet();
        wallet.setFamilyId(family.getId());
        wallet.setBalance(520);
        wallet.setFrozen(0);
        wallet.setVersion(0);
        coinWalletMapper.insert(wallet);

        family.setWalletId(wallet.getId());
        familyMapper.updateById(family);

        FamilyMember member = new FamilyMember();
        member.setFamilyId(family.getId());
        member.setUserId(user.getId());
        member.setRole("owner");
        member.setNickname(user.getNickname());
        familyMemberMapper.insert(member);
    }

    private CoinWallet ensureWallet(Long familyId) {
        CoinWallet wallet = coinWalletMapper.selectOne(
            new LambdaQueryWrapper<CoinWallet>().eq(CoinWallet::getFamilyId, familyId).last("limit 1")
        );
        if (wallet == null) {
            wallet = new CoinWallet();
            wallet.setFamilyId(familyId);
            wallet.setBalance(0);
            wallet.setFrozen(0);
            wallet.setVersion(0);
            coinWalletMapper.insert(wallet);
        }
        return wallet;
    }

    private Long currentUserId() {
        User user = ensureCurrentUser(null);
        return user.getId();
    }

    private Long currentFamilyId() {
        Long userId = currentUserId();
        FamilyMember membership = familyMemberMapper.selectOne(
            new LambdaQueryWrapper<FamilyMember>().eq(FamilyMember::getUserId, userId).last("limit 1")
        );
        if (membership == null) {
            createDefaultFamily(ensureCurrentUser(null));
            membership = familyMemberMapper.selectOne(
                new LambdaQueryWrapper<FamilyMember>().eq(FamilyMember::getUserId, userId).last("limit 1")
            );
        }
        return membership.getFamilyId();
    }

    public Long getCurrentFamilyId() {
        return currentFamilyId();
    }

    public Long getCurrentUserId() {
        return currentUserId();
    }

    private DishDto toDishDto(Dish dish) {
        return new DishDto(
            dish.getId(),
            dish.getCategoryId(),
            dish.getName(),
            dish.getCover(),
            dish.getPrice(),
            dish.getRating(),
            parseTags(dish.getTags()),
            dish.getSpicyLevel()
        );
    }

    private CategoryDto toCategoryDto(Category category) {
        return new CategoryDto(
            category.getId(),
            category.getName(),
            category.getIcon(),
            category.getDescription(),
            category.getSortOrder(),
            category.getVisible() != null && category.getVisible() == 1
        );
    }

    private List<String> parseTags(String tags) {
        if (!StringUtils.hasText(tags)) {
            return Collections.emptyList();
        }
        return Arrays.asList(tags.split(","));
    }

    private String joinTags(List<String> tags) {
        if (CollectionUtils.isEmpty(tags)) {
            return null;
        }
        return String.join(",", tags);
    }

    private void applyDish(DishRequest request, Dish dish) {
        dish.setCategoryId(request.getCategoryId());
        dish.setName(request.getName());
        dish.setCover(request.getCover());
        dish.setDescription(request.getDescription());
        dish.setPrice(request.getPrice());
        dish.setRating(request.getRating() == null ? 5.0 : request.getRating());
        dish.setTags(joinTags(request.getTags()));
        dish.setSpicyLevel(request.getSpicyLevel());
        dish.setStatus(request.getStatus() == null ? 1 : request.getStatus());
    }

    private CartItemDto toCartItemDto(CartItem item, Dish dish) {
        return new CartItemDto(
            item.getDishId(),
            dish == null ? "未知" : dish.getName(),
            item.getQuantity(),
            item.getNote(),
            dish == null ? 0d : dish.getPrice()
        );
    }

    private OffsetDateTime toOffset(LocalDateTime time) {
        return time == null ? OffsetDateTime.now(ZONE_OFFSET) : time.atOffset(ZONE_OFFSET);
    }

    private String generateOrderNo() {
        return "P" + System.currentTimeMillis() + orderIdGenerator.incrementAndGet();
    }
}

