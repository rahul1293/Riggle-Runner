/*
 *  Copyright 2017 Google Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.rk_tech.riggle_runner.utils.event;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.rk_tech.riggle_runner.data.model.helper.Resource;

import org.jetbrains.annotations.NotNull;

public class SingleRequestEvent<T> extends MutableLiveData<Resource<T>> {

    public interface RequestObserver<T> {
        void onRequestReceived(@NonNull Resource<T> resource);
    }

    public void observe(LifecycleOwner owner, final RequestObserver<T> observer) {
        super.observe(owner, new Observer<Resource<T>>() {
            @Override
            public void onChanged(@Nullable Resource<T> resource) {
                if (resource == null) {
                    return;
                }

                observer.onRequestReceived(resource);
            }
        });
    }
}
