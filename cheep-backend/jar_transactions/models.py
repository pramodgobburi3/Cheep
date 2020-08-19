# Create your models here.
from django.db import models
from jars.models import Jars


# Create your models here.
class JarTransactions(models.Model):
    jar = models.ForeignKey(Jars, on_delete=models.CASCADE)
    transaction_name = models.CharField(max_length=50)
    transaction_portion = models.DecimalField(max_digits=3, decimal_places=2)
    transaction_category = models.CharField(max_length=10)

    class Meta:
        managed = True
        db_table = "jar_transactions"