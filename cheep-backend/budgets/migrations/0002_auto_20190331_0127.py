# Generated by Django 2.1.5 on 2019-03-31 01:27

from django.db import migrations


class Migration(migrations.Migration):

    dependencies = [
        ('budgets', '0001_initial'),
    ]

    operations = [
        migrations.RenameField(
            model_name='budgets',
            old_name='balance',
            new_name='spent',
        ),
    ]
