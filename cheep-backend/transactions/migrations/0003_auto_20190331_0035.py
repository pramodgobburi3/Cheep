# Generated by Django 2.1.5 on 2019-03-31 00:35

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('transactions', '0002_auto_20190331_0033'),
    ]

    operations = [
        migrations.AlterField(
            model_name='transactions',
            name='transaction_category',
            field=models.CharField(max_length=250),
        ),
    ]
