from django.shortcuts import render
from django.views.decorators.csrf import csrf_exempt
from oauthlib.common import generate_token
from oauth2_provider.models import AccessToken, Application, RefreshToken
from django.utils import timezone
from django.http import JsonResponse
import json
from .models import Jars
from jar_categories.models import JarCategories
from .serializers import *

# Create your views here.
@csrf_exempt
def add_jar(request):
    if request.method == "POST":
        body = json.loads(request.body)
        if body["access_token"] is None:
            return JsonResponse({"status": "No access token provided"}, safe=False)
        else:
            try:
                access_token = AccessToken.objects.get(token=body["access_token"],
                                                       expires__gt=timezone.now())
            except:
                try:
                    access_token = AccessToken.objects.get(token=body["access_token"])
                except:
                    return JsonResponse({"status": "Invalid token"})
                else:
                    return JsonResponse({"status": "Access token has expired. Request a new one."})
            else:
                user = access_token.user
                jar_name = body["jar_name"]
                jar_amount = body["jar_amount"]
                jar_fraction = body["jar_fraction"]
                jar = Jars.objects.create(user_id=user.id, jar_name=jar_name, jar_amount=jar_amount, jar_fraction=jar_fraction)
                jar_categories = body["jar_categories"]

                for cat in jar_categories:
                    jcat = JarCategories.objects.create(jar=jar, category_id=cat)

                return JsonResponse({"status": "Successful", "jar": JarsSerializer(jar).data}, safe=False)

@csrf_exempt
def update_jars(request):
    if request.method == "POST":
        body = json.loads(request.body)
        if body["access_token"] is None:
            return JsonResponse({"status": "No access token provided"}, safe=False)
        else:
            try:
                access_token = AccessToken.objects.get(token=body["access_token"],
                                                       expires__gt=timezone.now())
            except:
                try:
                    access_token = AccessToken.objects.get(token=body["access_token"])
                except:
                    return JsonResponse({"status": "Invalid token"})
                else:
                    return JsonResponse({"status": "Access token has expired. Request a new one."})
            else:
                user = access_token.user
                jars = body["jars"]

                for jar in jars:
                    jar_id = jar["jar_id"]
                    jar_name = jar["jar_name"]
                    jar_amount = jar["jar_amount"]
                    jar_fraction = jar["jar_fraction"]

                    j = Jars.objects.get(id=jar_id)

                    if j.user_id == user.id:
                        j.jar_name = jar_name
                        j.jar_amount = jar_amount
                        j.jar_fraction = jar_fraction
                        j.save()

                js = Jars.objects.all()

                return JsonResponse({"status": "Successful", "jars": JarsSerializer(js, many=True)}, safe=False)