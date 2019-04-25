package trrakee.pnavw.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.utils.UrlBeaconUrlCompressor;

import trrakee.pnavw.util.BeaconUtil;

/**
 * Created by Tushar Sharma on 03/26/2019.
 */
public class DetectedBeacon extends Beacon implements IManagedBeacon {

    public static final int TYPE_EDDYSTONE_UID = 0;
    public static final int TYPE_EDDYSTONE_URL = 16;
    private static final int TYPE_IBEACON_ALTBEACON = 1;
    public static final Parcelable.Creator<DetectedBeacon> CREATOR =
            new Parcelable.Creator<DetectedBeacon>() {
                @Override
                public DetectedBeacon createFromParcel(Parcel in) {
                    Beacon b = Beacon.CREATOR.createFromParcel(in);
                    DetectedBeacon dbeacon = new DetectedBeacon(b);
                    dbeacon.mLastSeen = in.readLong();
                    return dbeacon;
                }

                @Override
                public DetectedBeacon[] newArray(int size) {
                    return new DetectedBeacon[size];
                }
            };
    private static final int TYPE_EDDYSTONE_TLM = 32;
    private long mLastSeen;

    public DetectedBeacon(Beacon paramBeacon) {
        super(paramBeacon);
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        super.writeToParcel(parcel, flags);
        parcel.writeLong(mLastSeen);
    }


    @Override
    public long getTimeLastSeen() {
        return this.mLastSeen;
    }

    public void setTimeLastSeen(long lastSeen) {
        this.mLastSeen = lastSeen;
    }

    @Override
    public boolean equalTo(IManagedBeacon target) {
        return getId().equals(target.getId());
    }

    @Override
    public boolean isEddyStoneTLM() {
        return getBeaconTypeCode() == TYPE_EDDYSTONE_TLM;
    }

    @Override
    public boolean isEddyStoneUID() {
        return getBeaconTypeCode() == TYPE_EDDYSTONE_UID;
    }

    @Override
    public boolean isEddyStoneURL() {
        return getBeaconTypeCode() == TYPE_EDDYSTONE_URL;
    }

    @Override
    public boolean isEddyStoneBeacon() {
        return (getBeaconTypeCode() == TYPE_EDDYSTONE_UID)
                || (getBeaconTypeCode() == TYPE_EDDYSTONE_URL) || (getBeaconTypeCode() == TYPE_EDDYSTONE_TLM);
    }

    @Override
    public BeaconType getBeaconType() {
        if (isEddyStoneBeacon()) {
            switch (getBeaconTypeCode()) {
                case TYPE_IBEACON_ALTBEACON:
                    return BeaconType.ALTBEACON;
                case TYPE_EDDYSTONE_TLM:
                    return BeaconType.EDDYSTONE_TLM;
                case TYPE_EDDYSTONE_UID:
                    return BeaconType.EDDYSTONE_UID;
                case TYPE_EDDYSTONE_URL:
                    return BeaconType.EDDYSTONE_URL;
                default:
                    return BeaconType.EDDYSTONE;
            }
        }
        return BeaconType.IBEACON;
    }

    public Identifier getId2() {
        if (isEddyStoneURL()) {
            return Identifier.parse("");
        }
        return super.getId2();
    }

    public Identifier getId3() {
        if (isEddyStoneBeacon()) {
            return Identifier.parse("");
        }
        return super.getId3();
    }

    @Override
    public String getId() {
        return getUUID() + ";" + getMajor() + ";" + getMinor() + ";FF:FF:FF:FF:FF:FF"; // ";" + getBluetoothAddress();
    }

    @Override
    public int getType() {
        return getBeaconTypeCode();
    }

    @Override
    public String getUUID() {
        return getId1().toString();
    }


    @Override
    public String getMajor() {
        if (isEddyStoneBeacon()) {
            return getId2().toHexString();
        }
        return getId2().toString();
    }

    @Override
    public String getMinor() {
        return getId3().toString();
    }

    @Override
    public String getEddyStoneBeaconURL() {
        return UrlBeaconUrlCompressor.uncompress(getId1().toByteArray());
    }

    @Override
    public String toString() {
        if (isEddyStoneBeacon()) {
            if (isEddyStoneUID()) {
                return "Namespace: " + getUUID() + ", Instance: " + getMajor() + "\n" + "RSSI: " + getRssi() + " dBm, TX: " + getTxPower() + " dBm\n" + "Distance: " + BeaconUtil.getRoundedDistance(getDistance()) + "ft.";
            }
            if (isEddyStoneURL()) {
                return "URL: " + getEddyStoneBeaconURL() + "\n" + "RSSI: " + getRssi() + " dBm, TX: " + getTxPower() + " dBm\n" + "Distance: " + BeaconUtil.getRoundedDistance(getDistance()) + "ft.";
            }
            return "UUID: " + getUUID() + ", Major: " + getMajor() + ", Minor: " + getMinor() + "\n" + "RSSI: " + getRssi() + " dBm, TX: " + getTxPower() + " dBm\n" + "Distance: " + BeaconUtil.getRoundedDistance(getDistance()) + "ft.";
        }
        return "UUID: " + getUUID() + ", Major: " + getMajor() + ", Minor: " + getMinor() + "\n" + "RSSI: " + getRssi() + " dBm, TX: " + getTxPower() + " dBm\n" + "Distance: " + BeaconUtil.getRoundedDistance(getDistance()) + "ft.";
    }
}
