package org.jacorb.test.notification;

import junit.framework.Assert;

import org.omg.CORBA.Any;
import org.omg.CORBA.IntHolder;
import org.omg.CORBA.ORB;
import org.omg.CosEventChannelAdmin.AlreadyConnected;
import org.omg.CosEventComm.Disconnected;
import org.omg.CosNotification.EventType;
import org.omg.CosNotifyChannelAdmin.AdminLimitExceeded;
import org.omg.CosNotifyChannelAdmin.AdminNotFound;
import org.omg.CosNotifyChannelAdmin.ClientType;
import org.omg.CosNotifyChannelAdmin.EventChannel;
import org.omg.CosNotifyChannelAdmin.InterFilterGroupOperator;
import org.omg.CosNotifyChannelAdmin.ProxyPushConsumer;
import org.omg.CosNotifyChannelAdmin.ProxyPushConsumerHelper;
import org.omg.CosNotifyChannelAdmin.ProxyType;
import org.omg.CosNotifyChannelAdmin.SupplierAdmin;
import org.omg.CosNotifyComm.PushSupplierPOA;
import org.omg.CosNotifyFilter.Filter;
import org.omg.PortableServer.POA;

public class AnyPushSender
    extends PushSupplierPOA
    implements TestClientOperations, Runnable
{
    ORB orb_;
    POA poa_;

    ProxyPushConsumer myConsumer_;
    boolean connected_;
    Any event_;
    boolean error_;
    long sendTime_;
    TestEventGenerator generator_;
    PerformanceListener perfListener_;
    int runs_;
    long interval_;

    SupplierAdmin myAdmin_;
    NotificationTestCase testCase_;

    public AnyPushSender(NotificationTestCase testCase)
    {
        testCase_ = testCase;
    }

    void setInterval(int i)
    {
        interval_ = i;
    }

    void setEventsToSend(int i)
    {
        runs_ = i;
    }

    AnyPushSender(NotificationTestCase testCase, Any event) 
    {
        event_ = event;
        testCase_ = testCase;
    }

    public AnyPushSender(NotificationTestCase testCase,
                         PerformanceListener perfListener,
                         TestEventGenerator generator,
                         int runs,
                         long interval)
    {
        generator_ = generator;
        perfListener_ = perfListener;
        runs_ = runs;
        interval_ = interval;
        testCase_ = testCase;
    }

    public void addAdminFilter(Filter filter)
    {
        Assert.assertNotNull(myAdmin_);
        myAdmin_.add_filter(filter);
    }


    public void addProxyFilter(Filter filter)
    {
        Assert.assertNotNull(myConsumer_);
        myConsumer_.add_filter(filter);
    }

    public void disconnect_push_supplier()
    {
        connected_ = false;
    }

    public boolean isConnected()
    {
        return connected_;
    }

    public boolean isError()
    {
        return false;
    }

    public boolean isEventHandled()
    {
        return true;
    }

    public void subscription_change(EventType[] e1, EventType[] e2)
    {}

    public void run()
    {
        if (event_ != null)
        {
            singleSend();
        }
        else
        {
            multSend();
        }
    }

    public void multSend()
    {
        for (int x = 0; x < runs_; ++x)
        {
            Any _event = null;

            try
            {
                long _start, _stop;
                synchronized (generator_)
                {
                    _event = generator_.getNextEvent();
                    _start = System.currentTimeMillis();
                    myConsumer_.push(_event);
                    _stop = System.currentTimeMillis();
                }

                perfListener_.eventSent(_event, System.currentTimeMillis(),
                                        _stop - _start);

                try
                {
                    Thread.sleep(interval_);
                }
                catch (InterruptedException ie)
                {}
            }
            catch (Exception e)
            {
                if (perfListener_ != null)
                {
                    perfListener_.eventFailed(_event, e);
                }
            }
        }
    }

    public void singleSend()
    {
        try
        {
            myConsumer_.push(event_);
            sendTime_ = System.currentTimeMillis();
        }
        catch (Disconnected d)
        {
            error_ = true;
        }
    }

    public void connect(EventChannel channel,
                        boolean useOrSemantic)

        throws AdminLimitExceeded,
               AlreadyConnected,
               AdminNotFound
    {

        IntHolder _proxyId = new IntHolder();
        IntHolder _adminId = new IntHolder();

        if (useOrSemantic)
        {
            myAdmin_ = channel.new_for_suppliers(InterFilterGroupOperator.OR_OP, _adminId);
            Assert.assertEquals(InterFilterGroupOperator.OR_OP, myAdmin_.MyOperator());
        }
        else
        {
            myAdmin_ = channel.new_for_suppliers(InterFilterGroupOperator.AND_OP, _adminId);
        }

        Assert.assertEquals(myAdmin_, channel.get_supplieradmin(_adminId.value));

        myConsumer_ =
            ProxyPushConsumerHelper.narrow(myAdmin_.obtain_notification_push_consumer(ClientType.ANY_EVENT, _proxyId));

        Assert.assertEquals(ProxyType._PUSH_ANY, myConsumer_.MyType().value());

        myConsumer_.connect_any_push_supplier(_this(testCase_.getORB()));

        connected_ = true;
    }


    public void shutdown()
    {
        myConsumer_.disconnect_push_consumer();

        if (myAdmin_ != null)
        {
            myAdmin_.destroy();
        }
    }

}
