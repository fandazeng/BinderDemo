package zeng.fanda.com.binderdemo.server;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import zeng.fanda.com.binderdemo.Book;
import zeng.fanda.com.binderdemo.proxy.Proxy;

/**
 * 抽象类，继承Binder，拥有跨进程通信能力，具体提供的服务功能由实现类自身处理
 *
 * @author 曾凡达
 * @date 2019/2/13
 */
public abstract class Stub extends Binder implements IBookManager {
    //定义字符描述
    public static final String DESCRIPTOR = " zeng.fanda.com.binderdemo.BookManager";

    //定义函数编码,在跨进程调用的时候，不会传递函数而是传递编号来指明要调用哪个函数）
    public static final int GET_BOOKS = IBinder.FIRST_CALL_TRANSACTION;
    public static final int ADD_BOOK = IBinder.FIRST_CALL_TRANSACTION + 1;

    public Stub( ) {
        // 1. 将（descriptor，IBookManager）作为（key,value）对存入到Binder对象中的一个Map<String,IInterface>对象中
        // 2. 之后，Binder对象 可根据descriptor通过queryLocalIInterface（）获得对应IInterface对象（即plus）的引用，
        // 可依靠该引用完成对请求方法的调用
        this.attachInterface(this, DESCRIPTOR);
    }

    @Override
    protected boolean onTransact(int code, @NonNull Parcel data, @Nullable Parcel reply, int flags) throws RemoteException {
        //跨进程通信时，当client 通过 transact 方法请求时，驱动会通知对数据进行解包，
        // 最后会回调这个方法进行处理，该方法在服务端 binder 线程池中运行
        // 这人方法返回 false 时，客户端请求会失败，可以用来做权限验证，避免随便一个进程都能远程调用我们的服务

        switch (code) {
            case INTERFACE_TRANSACTION:
                reply.writeString(DESCRIPTOR);
                return true;
            case GET_BOOKS:
                //与 Proxy 类中的 data.writeInterfaceToken(DESCRIPTOR);配对使用
                data.enforceInterface(DESCRIPTOR);
                List<Book> result = this.getBooks();
                // 返回结果，驱动会唤醒 Client 端线程来获取执行结果
                reply.writeNoException();
                reply.writeTypedList(result);
                return true;
            case ADD_BOOK:
                //与 Proxy 类中的 data.writeInterfaceToken(DESCRIPTOR);配对使用
                data.enforceInterface(DESCRIPTOR);
                Book book = null;
                if (data.readInt() != 0) {
                    //反序列化，拿到数据
                    book = Book.CREATOR.createFromParcel(data);
                }
                //调用服务方法，具体功能实现在RemoteService中
                this.addBook(book);
                // 返回结果，驱动会唤醒 Client 端线程来获取执行结果
                reply.writeNoException();
                return true;
        }

        return super.onTransact(code, data, reply, flags);
    }


    @Override
    public IBinder asBinder() {
        //返回当前 binder 对象
        return this;
    }

    /**
     * 将  binder 对象 转化为相应的接口对象，区分进程，同一进程，直接返回当前对象，
     * 不同进程，返回代理对象
     */
     public static IBookManager asInterface(IBinder binder) {
         if (binder == null) {
             return null;
         }
         // 之前调用过 attachInterface() ，这里可以拿到引用
         IInterface iin = binder.queryLocalInterface(DESCRIPTOR);
         if (iin != null && iin instanceof IBookManager) {
             //同一进程，直接返回本地binder
             return (IBookManager) iin;
         } else {
             //跨进程，返回代理对象
             return new Proxy(binder);
         }
    }
}
