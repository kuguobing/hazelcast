/*
 * Copyright (c) 2008-2013, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.nio;

import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.DataAdapter;
import com.hazelcast.nio.serialization.SerializationContext;

import java.nio.ByteBuffer;

/**
 * A Packet is a piece of data send over the line.
 */
public final class Packet extends DataAdapter implements SocketWritable, SocketReadable {

    public static final byte VERSION = 1;

    private static final int stVersion = stBit++;
    private static final int stHeader = stBit++;
    private static final int stPartition = stBit++;

    public static final int HEADER_OP = 0;
    public static final int HEADER_RESPONSE = 1;
    public static final int HEADER_EVENT = 2;
    public static final int HEADER_WAN_REPLICATION = 3;
    public static final int HEADER_URGENT = 4;

    private short header;
    private int partitionId;

    private transient Connection conn;

    public Packet(SerializationContext context) {
        super(context);
    }

    public Packet(Data value, SerializationContext context) {
        this(value, -1, context);
    }

    public Packet(Data value, int partitionId, SerializationContext context) {
        super(value, context);
        this.partitionId = partitionId;
    }

    /**
     * Gets the Connection this Packet was send with.
     *
     * @return the Connection. Could be null.
     */
    public Connection getConn() {
        return conn;
    }

    /**
     * Sets the Connection this Packet is send with.
     *
     * This is done on the reading side of the Packet to make it possible to retrieve information about
     * the sender of the Packet.
     *
     * @param conn the connection.
     */
    public void setConn(final Connection conn) {
        this.conn = conn;
    }

    public void setHeader(int bit) {
        header |= 1 << bit;
    }

    public boolean isHeaderSet(int bit) {
        return (header & 1 << bit) != 0;
    }

    /**
     * Returns the header of the Packet. The header is used to figure out what the content is of this Packet before
     * the actual payload needs to be processed.
     *
     * @return  the header.
     */
    public short getHeader() {
        return header;
    }

    /**
     * Returns the partition id of this packet. If this packet is not for a particular partition, -1 is returned.
     *
     * @return the partition id.
     */
    public int getPartitionId() {
        return partitionId;
    }

    @Override
    public boolean isUrgent(){
        return isHeaderSet(HEADER_URGENT);
    }

    @Override
    public final boolean writeTo(ByteBuffer destination) {
        if (!isStatusSet(stVersion)) {
            if (!destination.hasRemaining()) {
                return false;
            }
            destination.put(VERSION);
            setStatus(stVersion);
        }
        if (!isStatusSet(stHeader)) {
            if (destination.remaining() < 2) {
                return false;
            }
            destination.putShort(header);
            setStatus(stHeader);
        }
        if (!isStatusSet(stPartition)) {
            if (destination.remaining() < 4) {
                return false;
            }
            destination.putInt(partitionId);
            setStatus(stPartition);
        }
        return super.writeTo(destination);
    }

    @Override
    public final boolean readFrom(ByteBuffer source) {
        if (!isStatusSet(stVersion)) {
            if (!source.hasRemaining()) {
                return false;
            }
            byte version = source.get();
            setStatus(stVersion);
            if (VERSION != version) {
                throw new IllegalArgumentException("Packet versions are not matching! This -> "
                        + VERSION + ", Incoming -> " + version);
            }
        }
        if (!isStatusSet(stHeader)) {
            if (source.remaining() < 2) {
                return false;
            }
            header = source.getShort();
            setStatus(stHeader);
        }
        if (!isStatusSet(stPartition)) {
            if (source.remaining() < 4) {
                return false;
            }
            partitionId = source.getInt();
            setStatus(stPartition);
        }
        return super.readFrom(source);
    }

    /**
     * Returns an estimation of the packet, including its payload, in bytes.
     *
     * @return the size of the packet.
     */
    public int size() {
        return (data != null  ? data.totalSize() : 0) + 7; // 7 = byte(version) + short(header) + int(partitionId)
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Packet{");
        sb.append("header=").append(header);
        sb.append(", partitionId=").append(partitionId);
        sb.append(", conn=").append(conn);
        sb.append('}');
        return sb.toString();
    }
}
