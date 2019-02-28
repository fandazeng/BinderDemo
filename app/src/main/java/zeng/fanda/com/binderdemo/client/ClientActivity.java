package zeng.fanda.com.binderdemo.client;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.List;

import zeng.fanda.com.binderdemo.Book;
import zeng.fanda.com.binderdemo.R;
import zeng.fanda.com.binderdemo.server.IBookManager;
import zeng.fanda.com.binderdemo.server.RemoteService;
import zeng.fanda.com.binderdemo.server.Stub;


public class ClientActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mGetBooks;
    private Button mAddBook;
    private boolean isServiceConnected;
    private IBookManager mBookManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGetBooks = findViewById(R.id.btn_get_books);
        mAddBook = findViewById(R.id.btn_add_book);

        mGetBooks.setOnClickListener(this);
        mAddBook.setOnClickListener(this);

        //绑定服务，即获取远程服务
        Intent intent = new Intent("zeng.fanda.com.binderdemo.remote.service");
        intent.setClass(this, RemoteService.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_get_books:
                if (isServiceConnected) {
                    try {
                        List<Book> books = mBookManager.getBooks();
                        Log.d("client", "书的数量" + books.size());
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.btn_add_book:
                if (isServiceConnected) {
                    try {
                        mBookManager.addBook(new Book(66, "流浪地球"));
                    } catch (RemoteException e) {
                    }
                }
                break;
        }
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            isServiceConnected = true;
            mBookManager = Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isServiceConnected = false;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isServiceConnected) {
            unbindService(mServiceConnection);
        }
    }
}
