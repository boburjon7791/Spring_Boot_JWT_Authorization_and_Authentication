package com.example.demo.logger;

import com.example.demo.item.Item;
import com.example.demo.store.Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;


public class ItemLogger {
    static File file=null;
    static {
        Path projectPath = Paths.get("");
        String absolutePath = projectPath.toAbsolutePath().toString();
        int i = absolutePath.lastIndexOf("\\");
        String substring = absolutePath.substring(0, i);
        System.out.println(substring);
        file = new File(substring+"/log.log");
        if (!file.exists()) {
            try {
                System.out.println(file.createNewFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println(file.getAbsolutePath());
    }


    private static final Logger logger = LoggerFactory.getLogger(ItemLogger.class);


    public static void updateEntity(Item entity) {
        final String log="Entity yangilandi: {} ";
        // Entityni yangilash logi
        logger.info(log,entity);
        writer(log+entity);

        // Yangilash logi boshqa ma'lumotlar bilan ham yozilishi mumkin
        logger.debug("Entity yangilandi: {} -> {}", entity);

        // Xatolarni log qilish uchun error darajasi ham ishlatilishi mumkin
        try {
            // Entityni yangilash logi
            logger.info("Entity yangilandi: {}", entity);
            writer(log+entity);
            // Yangilash logi boshqa ma'lumotlar bilan ham yozilishi mumkin
            logger.debug("Entity yangilandi: {} -> {}", entity);
        } catch (Exception e) {
            final String log2="Entity yangilashda xatolik yuz berdi";
            writer(log2+entity+" "+e.getMessage()+" "+e+" "+ Arrays.toString(e.getStackTrace())+" "+e.getCause());
            logger.error(log2, e);
        }
    }

    private static void writer(String s) {
        try {
            FileWriter writer = new FileWriter(file,true);
            writer.write(s+"\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void createEntity(Item entity) {
        // Entityni yaratish logi
        String log ="Entity yaratildi: {} "+entity;
        logger.info(log, entity);
        writer(log);
        // Yaratish logi boshqa ma'lumotlar bilan ham yozilishi mumkin
        logger.debug("Entity yaratildi: {} -> {}", entity.getId(), entity);
//        writer(log);
    }
    public static void readEntity(Item entity) {
        // Entityni o'qish logi
        String log ="Entity o'qildi: {} "+entity;
        logger.info(log, entity);
        writer(log);
        // O'qish logi boshqa ma'lumotlar bilan ham yozilishi mumkin
        logger.debug("Entity o'qildi: {} -> {}", entity.getId(), entity);
//        writer(log);
    }
    public static void deleteEntity(Item entity) {
        // Entityni o'chirish logi
        String log ="Entity o'chirildi: {} "+entity;
        logger.info(log, entity);
        writer(log);
        // O'chirish logi boshqa ma'lumotlar bilan ham yozilishi mumkin
        logger.debug("Entity o'chirildi: {} -> {}", entity.getId(), entity);
//        writer(log);
    }
}
