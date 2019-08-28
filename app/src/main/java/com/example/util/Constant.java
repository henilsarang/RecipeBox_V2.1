package com.example.util;

import com.viaviweb.recipebox.BuildConfig;
import java.io.Serializable;

public class Constant implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String SERVER_URL = BuildConfig.server_url;

    public static final String IMAGE_PATH_URL = SERVER_URL + "images/";

    public static final String API_URL = SERVER_URL + "api.php";

    public static final String RATING_URL = SERVER_URL + "api_rating.php?recipe_rate&device_id=";

    public static final String PROFILE_URL = SERVER_URL + "api.php?user_profile&id=";

    public static final String PROFILE_EDIT_URL = SERVER_URL + "api.php?user_profile_update&user_id=";


    public static final String ARRAY_NAME = "RECIPE_APP";
    public static final String HOME_FEATURED_ARRAY = "featured_recipe";
    public static final String HOME_LATEST_ARRAY = "latest_recipe";
    public static final String HOME_MOST_ARRAY = "most_view_recipe";
    public static final String HOME_LATEST_CAT = "category_list";
    public static final String ARRAY_NAME_REVIEW = "Ratings";

    public static final String CATEGORY_ID = "cid";
    public static final String CATEGORY_NAME = "category_name";
    public static final String CATEGORY_IMAGE_SMALL = "category_image_thumb";
    public static final String CATEGORY_IMAGE_BIG = "category_image";
    public static final String CATEGORY_IMAGE_THUMB = "category_image_thumb";
    public static final String CATEGORY_IMAGE_ICON = "category_image_icon";

    public static final String SUB_CATEGORY_ID = "sid";
    public static final String SUB_CATEGORY_NAME = "sub_cat_name";
    public static final String SUB_CATEGORY_IMAGE = "sub_cat_image";

    public static final String LATEST_RECIPE_ID = "id";
    public static final String LATEST_RECIPE_CAT_ID = "cat_id";
    public static final String LATEST_RECIPE_TYPE = "recipe_type";
    public static final String LATEST_RECIPE_NAME = "recipe_name";
    public static final String LATEST_RECIPE_TIME = "recipe_time";
    public static final String LATEST_RECIPE_INGREDIENT = "recipe_ingredients";
    public static final String LATEST_RECIPE_DIRE = "recipe_direction";
    public static final String LATEST_RECIPE_IMAGE_BIG = "recipe_image_b";
    public static final String LATEST_RECIPE_IMAGE_SMALL = "recipe_image_s";
    public static final String LATEST_RECIPE_VIDEO_PLAY = "video_id";
    public static final String LATEST_RECIPE_VIEW = "recipe_views";
    public static final String LATEST_RECIPE_CAT_NAME = "category_name";
    public static final String LATEST_RECIPE_SUB_CAT_NAME = "sub_cat_name";
    public static final String LATEST_RECIPE_AVR_RATE = "rate_avg";
    public static final String LATEST_RECIPE_TOTAL_RATE = "total_rate";
    public static final String LATEST_RECIPE_URL = "video_url";

    public static final String REVIEW_NAME = "user_name";
    public static final String REVIEW_RATE = "rate";
    public static final String REVIEW_MESSAGE = "message";

    public static String LATEST_RECIPE_IDD;

    public static final String APP_NAME = "app_name";
    public static final String APP_IMAGE = "app_logo";
    public static final String APP_VERSION = "app_version";
    public static final String APP_AUTHOR = "app_author";
    public static final String APP_CONTACT = "app_contact";
    public static final String APP_EMAIL = "app_email";
    public static final String APP_WEBSITE = "app_website";
    public static final String APP_DESC = "app_description";
    public static final String APP_PRIVACY = "app_privacy_policy";
    public static final String APP_DEVELOP = "app_developed_by";
    public static final String APP_TAGLINE = "app_tagline";
    public static final String ADS_BANNER_ID="banner_ad_id";
    public static final String ADS_FULL_ID="interstital_ad_id";
    public static final String ADS_BANNER_ON_OFF="banner_ad";
    public static final String ADS_FULL_ON_OFF="interstital_ad";
    public static final String ADS_PUB_ID="publisher_id";
    public static final String ADS_CLICK="interstital_ad_click";
    public static final String APP_PACKAGE_NAME="package_name";

    public static String SAVE_TAG_LINE,SAVE_ADS_BANNER_ID,SAVE_ADS_FULL_ID,SAVE_ADS_BANNER_ON_OFF,SAVE_ADS_FULL_ON_OFF="false",SAVE_ADS_PUB_ID,SAVE_ADS_CLICK;

    public static int GET_SUCCESS_MSG;
    public static final String MSG = "msg";
    public static final String SUCCESS = "success";
    public static final String USER_NAME = "name";
    public static final String USER_ID = "user_id";
    public static final String USER_EMAIL = "email";
    public static final String USER_PHONE = "phone";

    public static final String DOWNLOAD_FOLDER_PATH="/Recipe/";
    public static int AD_COUNT=0;
}
