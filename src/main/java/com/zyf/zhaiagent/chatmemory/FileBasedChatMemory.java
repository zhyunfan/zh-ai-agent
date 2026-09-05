package com.zyf.zhaiagent.chatmemory;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.objenesis.strategy.StdInstantiatorStrategy;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
/**
 * 基于文件持久化的对话记忆
 */
public class FileBasedChatMemory implements ChatMemory {

    private final String BASE_DIR;//存储路径
    //初始化kryo对象（常量就行，不会变动）
    private static final Kryo kryo=new Kryo();

    static {
        kryo.setRegistrationRequired(false);//不需要手动注册
        //设置实例化策略
        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());//标准的实例化策略
    }

    public FileBasedChatMemory(String dir){
        this.BASE_DIR=dir;
        File baseDir=new File(dir);
        if(!baseDir.exists()){
            baseDir.mkdirs();
        }
    }

    //用户发送新消息或AI回复时	Spring AI框架自动调用
    @Override
    public void add(String conversationId, Message message) {
        ChatMemory.super.add(conversationId, message);
    }

    @Override
    public void add(String conversationId, List<Message> messages) {
        List<Message> conversationMessages = getOrCreateConversation(conversationId);
        conversationMessages.addAll(messages);
        saveConversation(conversationId, conversationMessages);
    }

    //每次用户发起新对话时	Spring AI框架自动调用
    @Override
    public List<Message> get(String conversationId) {
        List<Message> allMessages = getOrCreateConversation(conversationId);
        return allMessages.stream()
                .toList();
    }

    //用户主动清空对话时	业务代码手动调用
    @Override
    public void clear(String conversationId) {
        File file = getConversationFile(conversationId);
        if (file.exists()) {
            file.delete();
        }
    }

    private List<Message> getOrCreateConversation(String conversationId) {
        File file = getConversationFile(conversationId);
        List<Message> messages = new ArrayList<>();
        if (file.exists()) {
            try (Input input = new Input(new FileInputStream(file))) {
                messages = kryo.readObject(input, ArrayList.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return messages;
    }

    private void saveConversation(String conversationId, List<Message> messages) {
        File file = getConversationFile(conversationId);
        try (Output output = new Output(new FileOutputStream(file))) {
            kryo.writeObject(output, messages);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File getConversationFile(String conversationId) {
        return new File(BASE_DIR, conversationId + ".kryo");
    }
}

