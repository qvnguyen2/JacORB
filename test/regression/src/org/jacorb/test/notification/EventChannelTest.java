package org.jacorb.test.notification;

import junit.framework.Assert;
import junit.framework.Test;

import org.omg.CORBA.Any;
import org.omg.CORBA.IntHolder;
import org.omg.CORBA.TRANSIENT;
import org.omg.CosNotification.AnyOrder;
import org.omg.CosNotification.DiscardPolicy;
import org.omg.CosNotification.LifoOrder;
import org.omg.CosNotification.OrderPolicy;
import org.omg.CosNotification.Property;
import org.omg.CosNotifyChannelAdmin.AdminNotFound;
import org.omg.CosNotifyChannelAdmin.ClientType;
import org.omg.CosNotifyChannelAdmin.ConsumerAdmin;
import org.omg.CosNotifyChannelAdmin.EventChannel;
import org.omg.CosNotifyChannelAdmin.InterFilterGroupOperator;
import org.omg.CosNotifyChannelAdmin.ProxySupplier;
import org.omg.CosNotifyChannelAdmin.SupplierAdmin;

/**
 * @author Alphonse Bendt
 */

public class EventChannelTest extends NotificationTestCase
{
    private Any testPerson_;

    private EventChannel channel_;

    private SupplierAdmin supplierAdmin_;

    private ConsumerAdmin consumerAdmin_;

    /**
     * setup EventChannelFactory, FilterFactory and Any with Testdata
     */
    public void setUpTest() throws Exception
    {
        testPerson_ = getTestUtils().getTestPersonAny();

        channel_ = getDefaultChannel();

        supplierAdmin_ = channel_.default_supplier_admin();
        consumerAdmin_ = channel_.default_consumer_admin();
    }

    public void testDefaultAdmins() throws Exception
    {
        assertEquals(channel_, supplierAdmin_.MyChannel());
        assertEquals(channel_, consumerAdmin_.MyChannel());

        assertEquals(supplierAdmin_, channel_.default_supplier_admin());
        assertEquals(consumerAdmin_, channel_.default_consumer_admin());

        assertEquals(consumerAdmin_, channel_.get_consumeradmin(consumerAdmin_.MyID()));
        final int adminID = supplierAdmin_.MyID();

        assertEquals(supplierAdmin_, channel_.get_supplieradmin(adminID));

        assertTrue(containsValue(channel_.get_all_consumeradmins(), consumerAdmin_.MyID()));

        assertTrue(containsValue(channel_.get_all_supplieradmins(), supplierAdmin_.MyID()));
    }

    public void testCreateConsumerAdmin() throws Exception
    {
        IntHolder id = new IntHolder();
        ConsumerAdmin admin = channel_.new_for_consumers(InterFilterGroupOperator.AND_OP, id);

        assertEquals(InterFilterGroupOperator.AND_OP, admin.MyOperator());

        assertEquals(id.value, admin.MyID());

        assertTrue(containsValue(channel_.get_all_consumeradmins(), admin.MyID()));

        assertEquals(admin, channel_.get_consumeradmin(id.value));
    }

    public void testCreateSupplierAdmin() throws Exception
    {
        IntHolder id = new IntHolder();
        SupplierAdmin admin = channel_.new_for_suppliers(InterFilterGroupOperator.AND_OP, id);

        assertEquals(InterFilterGroupOperator.AND_OP, admin.MyOperator());

        assertEquals(id.value, admin.MyID());

        assertTrue(containsValue(channel_.get_all_supplieradmins(), admin.MyID()));

        assertEquals(admin, channel_.get_supplieradmin(id.value));
    }

    public void testDestroyAdmin() throws Exception
    {
        IntHolder id = new IntHolder();
        SupplierAdmin admin1 = channel_.new_for_suppliers(InterFilterGroupOperator.AND_OP, id);

        IntHolder id2 = new IntHolder();
        SupplierAdmin admin2 = channel_.new_for_suppliers(InterFilterGroupOperator.AND_OP, id2);

        assertEquals(admin1, channel_.get_supplieradmin(id.value));

        admin1.destroy();

        try
        {
            channel_.get_supplieradmin(id.value);
            fail();
        } catch (AdminNotFound e)
        {
        }

        assertEquals(admin2, channel_.get_supplieradmin(id2.value));
    }

    private boolean containsValue(final int[] array, final int value)
    {
        boolean seen = false;

        for (int i = 0; i < array.length; i++)
        {
            if (array[i] == value)
            {
                seen = true;
            }
        }
        return seen;
    }

    public EventChannelTest(String name, NotificationTestCaseSetup setup)
    {
        super(name, setup);
    }

    public void testSetQos() throws Exception
    {
        IntHolder ih = new IntHolder();

        ProxySupplier ps = consumerAdmin_.obtain_notification_push_supplier(
                ClientType.STRUCTURED_EVENT, ih);

        Property[] props = new Property[2];

        Any discardPolicy = getORB().create_any();
        Any orderPolicy = getORB().create_any();

        discardPolicy.insert_short(LifoOrder.value);
        orderPolicy.insert_short(AnyOrder.value);

        props[0] = new Property(DiscardPolicy.value, discardPolicy);
        props[1] = new Property(OrderPolicy.value, orderPolicy);

        ps.set_qos(props);

        Property[] new_props = ps.get_qos();

        for (int x = 0; x < new_props.length; ++x)
        {
            if (new_props[x].name.equals(DiscardPolicy.value))
            {
                assertEquals(discardPolicy, new_props[x].value);
            }

            if (new_props[x].name.equals(OrderPolicy.value))
            {
                assertEquals(orderPolicy, new_props[x].value);
            }
        }
    }

    public void testGetConsumerAdmin() throws Exception
    {
        channel_.default_consumer_admin();
        channel_.get_consumeradmin(0);

        int[] _allKeys = channel_.get_all_consumeradmins();

        assertContains(0, _allKeys);

        IntHolder ih = new IntHolder();
        channel_.new_for_consumers(InterFilterGroupOperator.AND_OP, ih);

        _allKeys = channel_.get_all_consumeradmins();
        assertContains(ih.value, _allKeys);

        try
        {
            channel_.get_consumeradmin(Integer.MIN_VALUE);
            fail();
        } catch (AdminNotFound e)
        {
        }

        ConsumerAdmin c3 = channel_.get_consumeradmin(ih.value);

        assertEquals(ih.value, c3.MyID());
    }

    public void testGetSupplierAdmin() throws Exception
    {
        channel_.default_supplier_admin();
        channel_.get_supplieradmin(0);

        int[] _allKeys = channel_.get_all_supplieradmins();

        assertContains(0, _allKeys);

        IntHolder ih = new IntHolder();
        channel_.new_for_suppliers(InterFilterGroupOperator.AND_OP, ih);

        _allKeys = channel_.get_all_supplieradmins();
        assertContains(ih.value, _allKeys);

        try
        {
            channel_.get_supplieradmin(Integer.MIN_VALUE);
            fail();
        } catch (AdminNotFound e)
        {
        }

        SupplierAdmin c3 = channel_.get_supplieradmin(ih.value);
        assertEquals(ih.value, c3.MyID());
    }

    static void assertContains(int i, int[] is)
    {
        boolean seen = false;
        for (int x = 0; x < is.length; ++x)
        {
            if (is[x] == i)
            {
                seen = true;
            }
        }
        Assert.assertTrue(seen);
    }

    public void testSendEventPushPull() throws Exception
    {
        AnyPullReceiver _receiver = new AnyPullReceiver(this);
        _receiver.connect(channel_, false);
        AnyPushSender _sender = new AnyPushSender(this, testPerson_);
        _sender.connect(channel_, false);

        Thread _receiverThread = new Thread(_receiver);
        _receiverThread.start();
        _sender.run();

        _receiverThread.join();
        assertTrue(!_receiver.isError());

        assertTrue(_receiver.isEventHandled());

        _receiver.shutdown();
        _sender.shutdown();
    }

    public void testSendEventPushPush() throws Exception
    {

        // start a receiver thread
        AnyPushReceiver _receiver = new AnyPushReceiver(this);
        _receiver.connect(channel_, false);

        Thread _receiverThread = new Thread(_receiver);

        // start a sender
        AnyPushSender _sender = new AnyPushSender(this, testPerson_);

        _sender.connect(channel_, false);

        _receiverThread.start();

        _sender.run();

        _receiverThread.join();

        assertTrue(_receiver.isEventHandled());

        _receiver.shutdown();
        _sender.shutdown();
    }

    public void testSendEventPushPush_MisbehavingConsumer() throws Exception
    {
        // start a receiver thread
        AnyPushReceiver _receiver = new AnyPushReceiver(this)
        {
            public void push(Any any)
            {
                throw new TRANSIENT();
            }
        };
        _receiver.connect(channel_, false);

        Thread _receiverThread = new Thread(_receiver);

        // start a sender
        AnyPushSender _sender = new AnyPushSender(this, testPerson_);

        _sender.connect(channel_, false);

        _receiverThread.start();

        _sender.run();

        Thread.sleep(20000);

        assertTrue(!_receiver.isConnected());

        _sender.shutdown();
    }

    public void testSendEventPullPush() throws Exception
    {
        AnyPullSender _sender = new AnyPullSender(this, testPerson_);
        _sender.connect(channel_, false);

        AnyPushReceiver _receiver = new AnyPushReceiver(this);
        _receiver.connect(channel_, false);

        Thread _receiverThread = new Thread(_receiver);
        _receiverThread.start();

        _sender.run();

        _receiverThread.join();

        assertTrue(_sender.isEventHandled());
        assertTrue(_receiver.isEventHandled());

        _receiver.shutdown();
        _sender.shutdown();

    }

    public void testSendEventPullPull() throws Exception
    {
        AnyPullSender _sender = new AnyPullSender(this, testPerson_);
        _sender.connect(channel_, false);

        AnyPullReceiver _receiver = new AnyPullReceiver(this);
        _receiver.connect(channel_, false);

        Thread _receiverThread = new Thread(_receiver);
        _receiverThread.start();

        _sender.run();

        _receiverThread.join();

        assertTrue(_sender.isEventHandled());
        assertTrue(_receiver.isEventHandled());

        _receiver.shutdown();
        _sender.shutdown();

    }

    /**
     * Test if all EventChannel Clients are disconnected when the Channel is Destroyed
     */
    public void testDestroyChannelDisconnectsClients() throws Exception
    {
        IntHolder _id = new IntHolder();

        EventChannel _channel = getFactory().create_channel(new Property[0], new Property[0], _id);

        AnyPullReceiver _anyPullReceiver = new AnyPullReceiver(this);
        _anyPullReceiver.connect(_channel, false);

        AnyPushReceiver _anyPushReceiver = new AnyPushReceiver(this);
        _anyPushReceiver.connect(_channel, false);

        AnyPullSender _anyPullSender = new AnyPullSender(this, testPerson_);
        _anyPullSender.connect(_channel, false);

        AnyPushSender _anyPushSender = new AnyPushSender(this, testPerson_);
        _anyPushSender.connect(_channel, false);

        assertTrue(_anyPullReceiver.isConnected());
        assertTrue(_anyPushReceiver.isConnected());
        assertTrue(_anyPullSender.isConnected());
        assertTrue(_anyPushSender.isConnected());

        _channel.destroy();

        assertTrue(!_anyPullReceiver.isConnected());
        assertTrue(!_anyPushReceiver.isConnected());
        assertTrue(!_anyPullSender.isConnected());
        assertTrue(!_anyPushSender.isConnected());
    }

    /**
     * Test if all EventChannel Clients are disconnected when the Channel is Destroyed
     */
    public void testDestroyAdminDisconnectsClients() throws Exception
    {
        IntHolder _id = new IntHolder();

        EventChannel _channel = getFactory().create_channel(new Property[0], new Property[0], _id);

        AnyPullReceiver _anyPullReceiver = new AnyPullReceiver(this);
        _anyPullReceiver.connect(_channel, false);

        AnyPushReceiver _anyPushReceiver = new AnyPushReceiver(this);
        _anyPushReceiver.connect(_channel, false);

        AnyPullSender _anyPullSender = new AnyPullSender(this, testPerson_);
        _anyPullSender.connect(_channel, false);

        AnyPushSender _anyPushSender = new AnyPushSender(this, testPerson_);
        _anyPushSender.connect(_channel, false);

        assertTrue(_anyPullReceiver.isConnected());
        assertTrue(_anyPushReceiver.isConnected());
        assertTrue(_anyPullSender.isConnected());
        assertTrue(_anyPushSender.isConnected());

        _anyPullReceiver.myAdmin_.destroy();
        _anyPushReceiver.myAdmin_.destroy();
        _anyPullSender.myAdmin_.destroy();
        _anyPushSender.myAdmin_.destroy();

        assertTrue(!_anyPullReceiver.isConnected());
        assertTrue(!_anyPushReceiver.isConnected());
        assertTrue(!_anyPullSender.isConnected());
        assertTrue(!_anyPushSender.isConnected());

        _channel.destroy();
    }

    public void testCreateChannel() throws Exception
    {
        IntHolder _id = new IntHolder();

        EventChannel _channel = getFactory().create_channel(new Property[0], new Property[0], _id);

        // test if channel id appears within channel list
        int[] _allFactories = getFactory().get_all_channels();
        boolean _seen = false;
        for (int x = 0; x < _allFactories.length; ++x)
        {
            if (_allFactories[x] == _id.value)
            {
                _seen = true;
            }
        }
        assertTrue(_seen);

        EventChannel _sameChannel = getFactory().get_event_channel(_id.value);
        assertTrue(_channel._is_equivalent(_sameChannel));

        _channel.destroy();
    }

    public static Test suite() throws Exception
    {
        return NotificationTestCase.suite("Basic CosNotification EventChannel Tests",
                EventChannelTest.class);
    }
}