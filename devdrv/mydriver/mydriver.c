
#if 1

/****************hellomod.c*******************************/
#include <linux/module.h> //����ģ�鶼��Ҫ��ͷ�ļ�
#include <linux/init.h> // init&exit��غ�
MODULE_LICENSE("GPL");
static int __init hello_init (void)
{
    printk("Hello china init/n");
    return 0;
}

static void __exit hello_exit (void)
{
    printk("Hello china exit/n");
}

module_init(hello_init);
module_exit(hello_exit);

#endif
/****************hellomod.c*******************************/


#if 0

//#define  __NO_VERSION__
#include<linux/version.h> /*�����Ǳ����������ͷ�ļ�*/
#include<linux/module.h>
#include<linux/config.h>
#include<asm-generic/uaccess.h>
#include<linux/types.h>
#include<linux/fs.h>
#include<linux/mm.h>
#include<linux/errno.h>
#include<asm-generic/segment.h>


#include "linux/kernel.h"  
#include "linux/module.h"  
#include "linux/fs.h"  
#include "linux/init.h"  
#include "linux/types.h"  
#include "linux/errno.h"  
#include "linux/uaccess.h"  
#include "linux/kdev_t.h" 

//#include <linux/module.h> 
//#include <linux/init.h>
//#include <linux/kernel.h>

#define BUFSIZE 256  /*�豸�а���������ַ���*/

char * temp;  /*��ָ������Ϊ���������豸�����ڴ�ռ�*/
unsigned int major = 0;


#if 1
static ssize_t device_read(struct file * file,char * buf,size_t count,loff_t * f_pos)
{
    int i;
    if(count>BUFSIZE)   /*���Ҫ���������Ŀ�����豸���е���Ŀ����ʾ������*/
    {
        printk("Can't Read , the Count is to Big !\n");
        return  -EFAULT;
    }
    for(i = 0; i < count; i++) /*����,���ж�������*/
    {
        __put_user(temp[i],buf);
        buf++;
    }
    return count;
}


static ssize_t device_write(struct file * file,const char * buf,size_t count,loff_t * f_pos)
{
    int i;
    if(count>BUFSIZE)   /*Ҫ��д�����Ŀ���豸������������ʾ������*/
    {
        printk("Can't Write , the Count is to Big\n");
        return  -EFAULT;
    }
    for(i = 0; i < count; i++) /*����,����д�����*/
    {
        __get_user(temp[i],buf);
        buf++;
    }
    return count;
}

static int device_open(struct inode * inode,struct file * file) /*���豸����*/
{
    temp = (char *)kmalloc(BUFSIZE,GFP_KERNEL);  /*Ϊ�豸�����ڴ�ռ�*/
    MOD_INC_USE_COUNT;
    return 0;
}

static int device_release(struct inode * inode,struct file * file)
{
    kfree(temp);   /*�ͷ��豸ռ���ڴ�ռ�*/
    MOD_DEC_USE_COUNT;
    return 0;
}

struct file_operations fops =   /*���file_operations�ṹ*/
{
read:
    device_read,
write:
    device_write,
open:
    device_open,
release:
    device_release
};

int init_module(void)  /*�Ǽ��豸����,insmodʱ����*/
{
    int num;
    num = register_chrdev(0,"mydriver",&fops); /*ϵͳ�Զ�����һ��δ��ռ�õ��豸��*/
    if(num < 0)      /*�Ǽ�δ�ɹ�,��ʾ������*/
    {
        printk("Can't Got the Major Number !\n");
        return num;
    }
    if(major == 0)
        major = num;
    return 0;
}

void cleanup_module(void)  /*�ͷ��豸����,rmmodʱ����*/
{
    unregister_chrdev(major,"mydriver");
}
#endif
#endif

