# Generated by Django 2.1.5 on 2019-04-02 23:58

from django.db import migrations


class Migration(migrations.Migration):

    dependencies = [
        ('jar_transactions', '0002_jartransactions_transaction_category'),
    ]

    operations = [
        migrations.RemoveField(
            model_name='jartransactions',
            name='benchmark_date',
        ),
        migrations.RemoveField(
            model_name='jartransactions',
            name='start_date',
        ),
    ]
