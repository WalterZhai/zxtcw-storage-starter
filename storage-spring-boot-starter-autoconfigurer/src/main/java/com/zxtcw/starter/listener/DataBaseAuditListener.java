package com.zxtcw.starter.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Field;
import java.util.Date;

/**
 * @comment
 * @author Walter(翟笑天)
 * @date 2021/3/5
 */
@Component
public class DataBaseAuditListener {

    static final Logger logger = LoggerFactory.getLogger(DataBaseAuditListener.class);

    @PrePersist
    public void prePersist(Object object)
            throws IllegalArgumentException, IllegalAccessException {
        // 如果填充字段被分装在一个父类中： Class<?> aClass = object.getClass().getSuperclass();
        Class<?> aClass = object.getClass();
        try {
            // 填充创建用户Id
            addUserId(object, aClass, "createId");
            // 填充创建时间
            addOperateTime(object, aClass, "createDate");
            // 填充更新用户Id
            addUserId(object, aClass, "modifyId");
            // 填充更新时间
            addOperateTime(object, aClass, "modifyDate");
        } catch (NoSuchFieldException e) {
            logger.error("反射获取属性异常：", e);
        }


    }

    /**
     * @comment 更新数据时，填充更新人和更新时间
     * @author Walter(翟笑天)
     * @date 2021/3/5
     */
    @PreUpdate
    public void preUpdate(Object object)
            throws IllegalArgumentException, IllegalAccessException {
        Class<?> aClass = object.getClass();
        try {
            // 填充更新用户Id
            addUserId(object, aClass, "modifyId");
            // 填充更新时间
            addOperateTime(object, aClass, "modifyDate");
        } catch (NoSuchFieldException e) {
            logger.error("反射获取属性异常：", e);
        }
    }

    /**
     * @comment 新增数据之后的操作
     * @author Walter(翟笑天)
     * @date 2021/3/5
     */
    @PostPersist
    public void postPersist(Object object)
            throws IllegalArgumentException, IllegalAccessException {

    }

    /**
     * @comment 更新数据之后的操作
     * @author Walter(翟笑天)
     * @date 2021/3/5
     */
    @PostUpdate
    public void postUpdate(Object object)
            throws IllegalArgumentException, IllegalAccessException {
    }

    /**
     * @comment 填充用户id
     * @author Walter(翟笑天)
     * @date 2021/3/5
     */
    protected void addUserId(Object object, Class<?> aClass, String propertyName) throws NoSuchFieldException, IllegalAccessException {
        Field userId = aClass.getSuperclass().getDeclaredField(propertyName);
        userId.setAccessible(true);
        // 获取userId值
        Object userIdValue = userId.get(object);
        if (userIdValue == null) {
            //获得session
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            HttpSession session = request.getSession();
            //获得用户
            String username = (String) session.getAttribute("username");
            // 在此处使用当前用户id或默认用户id
            String id = username;
            userId.set(object, id);
        }
    }

    /**
     * @comment 填充操作时间
     * @author Walter(翟笑天)
     * @date 2021/3/5
     */
    protected void addOperateTime(Object object, Class<?> aClass, String propertyName) throws NoSuchFieldException, IllegalAccessException {
        Field time = aClass.getSuperclass().getDeclaredField(propertyName);
        time.setAccessible(true);
        // 获取time值
        Object createdTimeValue = time.get(object);
        if(createdTimeValue == null) {
            // 使用当前时间进行填充
            time.set(object, new Date());
        }
    }

}
