# Generated by Django 2.1.5 on 2019-04-02 23:58

from django.db import migrations, models
import django.utils.timezone


class Migration(migrations.Migration):

    dependencies = [
        ('jars', '0001_initial'),
    ]

    operations = [
        migrations.AddField(
            model_name='jars',
            name='start_date',
            field=models.DateTimeField(auto_now_add=True, default=django.utils.timezone.now),
            preserve_default=False,
        ),
    ]