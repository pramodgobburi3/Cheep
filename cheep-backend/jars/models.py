from django.db import models
from django.contrib.auth.models import User

# Create your models here.
class Jars(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    jar_name = models.CharField(max_length=50)
    jar_amount = models.DecimalField(max_digits=11, decimal_places=2)
    jar_fraction = models.DecimalField(max_digits=11, decimal_places=2)
    start_date = models.DateTimeField(auto_now_add=True)

    class Meta:
        managed = True
        db_table = "jars"