package com.tencent.sms;

import java.io.Serializable;

/*
 * ʵ��Serializable�ӿڣ��������ݵĴ���
 * �ں�����Ҫ����Message.obj=MessageItem�Ķ���������
 * ��ô�ͱ���ʵ�ִ˽ӿ�
 */
public class MessageItem implements Serializable{

	//����ID
	private int id;
	//��������   1�ǽ��յ��ģ�2�Ƿ�����
	private int type;
	//����Э�� ��0����\ 1����
	private int protocol;
	//����ʱ��
	private long date;
	//�ֻ���
	private String phone;
	//����
	public String body;
	
	public MessageItem()
	{
		
	}
	
	public MessageItem(int id,int type,int protocol,long date,String phone,String body)
	{
		this.id=id;
		this.type=type;
		this.protocol=protocol;
		this.date=date;
		this.phone=phone;
		this.body=body;
	}
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * @return the protocol
	 */
	public int getProtocol() {
		return protocol;
	}

	/**
	 * @param protocol the protocol to set
	 */
	public void setProtocol(int protocol) {
		this.protocol = protocol;
	}
	/**
	 * @return the date
	 */
	public long getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(long date) {
		this.date = date;
	}

	/**
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * @param phone the phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * @return the body
	 */
	public String getBody() {
		return body;
	}

	/**
	 * @param body the body to set
	 */
	public void setBody(String body) {
		this.body = body;
	}

	public String toString()
	{
		return "id="+id+",type="+type+",protocol="+protocol+",phone="+phone+",body="+body;
	}
}
