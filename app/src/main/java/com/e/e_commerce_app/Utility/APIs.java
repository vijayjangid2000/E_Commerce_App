package com.e.e_commerce_app.Utility;

public class APIs {
    public static String BASE_URL = "  http://foodnjoy.tk/mobileapp/api/";

    public static String IMG_URL = "http://foodnjoy.tk//";
    public static String URL_IMAGE = "http://foodnjoy.tk/products/large/";
    public static String URL_CART_IMAGE = "http://foodnjoy.tk/products/small/";
    public static String URL_CATEGORY_IMG = "http://foodnjoy.tk/category/small/";

    public static String LOGIN_URL = BASE_URL + "login";
    public static String REGISTER_URL = BASE_URL + "register";
    public static String FORGET_PASSWORD = BASE_URL + "forgot_password";
    public static String LOGOUT = BASE_URL + "logout";
    public static String UPDATE_PASSWORD = BASE_URL + "updatepassword?";

    public static String GET_CATEGORY = BASE_URL + "get_category_list";
    public static String GET_SLIDER = BASE_URL + "get_slider_list";
    public static String PRODUCT_LIST = BASE_URL + "get_product_list";
    public static String GET_ORDER_LIST = BASE_URL + "get_orders_list";

    public static String GET_CATEGORY_PRODUCT = BASE_URL + "get_category_product";
    public static String GET_FILTER_PRODUCT = BASE_URL + "get_product_filter";
    public static String GET_PRODUCT_DETAILS = BASE_URL + "get_product_detail";

    public static String ADD_CART = BASE_URL + "add_to_cart";
    public static String PLACE_ORDER_URL = BASE_URL + "place_order";
    public static String DELETE_ITEM = BASE_URL + "delete_cart_item";
    public static String CHECKOUT = BASE_URL + "checkout_order_data";
    public static String GET_ORDER_DETAILS = BASE_URL + "get_orders_detail";

    public static String UPDATE_PROFILE = BASE_URL + "updateprofile";
    public static String GET_PROFILE = BASE_URL + "get_profile_info";

    public static String STOCK_AVAILABLE = BASE_URL + "get_product_stock";
    public static String UPDATE_QUANTITY = BASE_URL + "update_quantity";
    public static String ADD_WISH_LIST = BASE_URL + "add_to_wishlist";
    public static String GET_WISH_LIST = BASE_URL + "get_wishlist";
    public static String DELETE_WISHLIST_ITEM = BASE_URL + "delete_wishlist";

    public static String DELETE_ADDRESS = BASE_URL + "address_delete";
    public static String GET_COUNTRY = BASE_URL + "get-country-list";
    public static String GET_STATE_LIST = BASE_URL + "get-state-list";
    public static String GET_CITY_LIST = BASE_URL + "get-city-list";

    public static String SEND_ISSUE = BASE_URL + "add_help_center_data";
    public static String ORDER_STATUS_CHANGE = BASE_URL + "order_status_change";
    public static String UPDATE_REWARD = BASE_URL + "update_reward_scratch";

    public static String GET_CANCEL_ORDER = BASE_URL + "get_orders_detail";
    public static String GET_REWARDS = BASE_URL + "get_user_rewards";
    public static String GET_OFFERS = BASE_URL + "get_user_offers";
    public static String GET_NOTIFICATIONS = BASE_URL + "get_user_notification";
    public static String VERIFY_COUPON = BASE_URL + "verify-coupon";
    public static String ADD_ADDRESS = BASE_URL + "set_profile_address";

    public static String APPLY_COUPON = BASE_URL + "apply-coupen-code";
    public static String GET_CART_TOTAL_ITEM = BASE_URL + "get_user_notification";

}
